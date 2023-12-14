package com.istif_backend.service;

import com.istif_backend.configuration.LocalDateParser;
import com.istif_backend.model.Comment;
import com.istif_backend.model.Istif;
import com.istif_backend.model.User;
import com.istif_backend.repository.IstifRepository;
import com.istif_backend.request.IstifCreateRequest;
import com.istif_backend.request.IstifEditRequest;
import com.istif_backend.response.IstifListResponse;
import com.istif_backend.response.SingleIstifResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class IstifService {
    @Autowired
    IstifRepository istifRepository;

    @Autowired
    CommentService commentService;

    @Autowired
    UserService userService;

    @Autowired
    ImageService imageService;


    public List<IstifListResponse> findAllByOrderByIdDesc(){
        List<Istif> istifList = istifRepository.findByShareFlagOrderByCreatedAtDesc(1);
        List<IstifListResponse> istifResponseList = new ArrayList<>();
        for(Istif istif: istifList){
            istifResponseList.add(new IstifListResponse(istif));
        }
        return istifResponseList;
    }

    public Istif createIstif(User foundUser, IstifCreateRequest istifCreateRequest) throws ParseException, IOException {
        Istif createdIstif = Istif.builder()
                .title(istifCreateRequest.getTitle())
                .titleLink(istifCreateRequest.getTitleLink())
                .labels(istifCreateRequest.getLabels())
                .source(istifCreateRequest.getSource())
                .text(imageService.parseAndSaveImages(istifCreateRequest.getText()))
                .user(foundUser)
                .shareFlag(istifCreateRequest.getShareFlag())
                .likes(new HashSet<>())
                .build();
        Istif modifiedIstif = LocalDateParser.parseDate(istifCreateRequest.getRelevantDate(),createdIstif);
        if(modifiedIstif.getTitle() == null || (modifiedIstif.getTitle().isEmpty() || modifiedIstif.getTitle().isBlank())){
            String title = fetchTitle(istifCreateRequest.getTitleLink());
            if(!title.isEmpty()){
                modifiedIstif.setTitle(title);
            }
            else{
                modifiedIstif.setTitle(modifiedIstif.getTitleLink());
            }
        }
        return istifRepository.save(modifiedIstif);

    }

    public List<IstifListResponse> findByUserIdOrderByIdDesc(Long userId){
        List<Istif> istifList = istifRepository.findByUserIdOrderByIdDesc(userId);
        List<IstifListResponse> istifResponseList = new ArrayList<>();
        for(Istif istif: istifList){
            istifResponseList.add(new IstifListResponse(istif));
        }
        return istifResponseList;
    }


    public Istif getIstifByIstifId(Long id) {
        Optional<Istif> optionalIstif = istifRepository.findById(id);
        if (optionalIstif.isEmpty()) {
            throw new NoSuchElementException("Istif with id '" + id + "' not found");
        }
        return optionalIstif.get();
    }

    public SingleIstifResponse retrieveIstif(Long id){
        Istif istif = getIstifByIstifId(id);
        return new SingleIstifResponse(istif);
    }

    public List<IstifListResponse> findFollowingStories(User foundUser) {
        Set<User> followingList = foundUser.getFollowing();
        List<Long> idList = new ArrayList<>();
        List<Istif> istifList = new ArrayList<>();
        for (User user : followingList){
            idList.add(user.getId());
        }
        for(Long id : idList){
            List<Istif> userIstifList = istifRepository.findByUserIdAndShareFlagOrderByIdDesc(id,1);
            istifList.addAll(userIstifList);
        }
        List<IstifListResponse> istifResponseList = new ArrayList<>();
        for(Istif istif: istifList){
            istifResponseList.add(new IstifListResponse(istif));
        }
        return istifResponseList;

    }


    public Istif likeIstif(Long istifId,Long userId){
        Istif istif = getIstifByIstifId(istifId);
        User user = userService.findByUserId(userId);
        Set<Long> likesList = istif.getLikes();
        Set<Long> likedList = user.getLikedIstifs();
        if(!likesList.contains(user.getId())){
            likesList.add(user.getId());
            likedList.add(istifId);
        }
        else{
            likesList.remove(user.getId());
            likedList.remove(istifId);
        }
        istif.setLikes(likesList);
        user.setLikedIstifs(likedList);
        userService.editUser(user);
        return istifRepository.save(istif);
    }

    public Set<Istif> searchIstifsWithQuery(String query) {
        Set<Istif> istifSet = new HashSet<>();
        istifSet.addAll(istifRepository.findByTitleContainingIgnoreCase(query));
        istifSet.addAll(istifRepository.findByLabelsContainingIgnoreCase(query));
        istifSet.addAll(istifRepository.findBySourceContainingIgnoreCase(query));
        return istifSet;
    }

    public List<Istif> searchIstifsWithDate(String date) throws ParseException {
        Date formattedDate = setToMidnight(stringToStartDate(date));
        Date formattedEndDate = setToEndOfDay(stringToEndDate(date));
        Set <Istif> results = new HashSet<>();
        results.addAll(istifRepository.findByCreatedAtBetween(formattedDate,formattedEndDate));
        results.addAll(istifRepository.findByRelevantDateBetween(
                LocalDateParser.convertDateToLocalDate(stringToStartDate(date)),
                LocalDateParser.convertDateToLocalDate(stringToEndDate(date))));
        return results.stream().toList();
    }

    public List<Istif> searchIstifsWithMultipleDate(String startDate,String endDate) throws ParseException {
        Date startFormattedDate = stringToStartDate(startDate);
        Date endFormattedDate = stringToEndDate(endDate);
        Set <Istif> results = new HashSet<>();
        results.addAll(istifRepository.findByCreatedAtBetween(startFormattedDate,endFormattedDate));
        results.addAll(istifRepository.findByRelevantDateBetween(
                LocalDateParser.convertDateToLocalDate(startFormattedDate),
                LocalDateParser.convertDateToLocalDate(endFormattedDate)));
        return results.stream().toList();
    }

    private Date setToMidnight(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }

    private Date setToEndOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);

        return calendar.getTime();
    }

    public String deleteByIstifId(Istif istif) {
        List<Comment> commentList = istif.getComments();
        for (Comment comment: commentList) {
            commentService.deleteComment(comment);
        }
        istifRepository.deleteById(istif.getId());
        return "deleted";
    }

    public Date stringToStartDate(String timeStamp) throws ParseException{
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        if(timeStamp.length() < 5){
            timeStamp += "-01-01";
        } else if (timeStamp.isBlank()) {
            return null;
        }
        return dateFormat.parse(timeStamp);

    }

    public Date stringToEndDate(String timeStamp) throws ParseException{
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        if(timeStamp.length() < 5){
            timeStamp += "-12-31";
        } else if (timeStamp.isBlank()) {
            return null;
        }
        return dateFormat.parse(timeStamp);

    }

    public Istif editIstif(IstifEditRequest request, User user, Long istifId) throws ParseException, IOException {
        Istif foundIstif = getIstifByIstifId(istifId);
        if(Objects.equals(foundIstif.getUser().getId(),user.getId())){
            Istif istif = LocalDateParser.parseDate(request.getIstifDate(),foundIstif);
            istif.setEditedAt(new Date());
            istif.setText(request.getText());
            istif.setTitle(request.getTitle());
            istif.setLabels(request.getLabels());
            istif.setShareFlag(request.getShareFlag());
            if(request.getTitle() == null || (request.getTitle().isEmpty() || request.getTitle().isBlank())){
                String title = fetchTitle(request.getTitleLink());
                if(!title.isEmpty()){
                    istif.setTitle(title);
                }
                else{
                    istif.setTitle(istif.getTitleLink());
                }
            }
            return istifRepository.save(istif);
        }
        return foundIstif;
    }

    public List<Istif> likedStories(User foundUser) {
        List<Long> likeList = new ArrayList<>(foundUser.getLikedIstifs());
        List<Istif> istifList = new ArrayList<>();
        for (Long istifId : likeList) {
            Istif istif = getIstifByIstifId(istifId);
            if (istif != null) {
                istifList.add(istif);
            }
        }
        return istifList;
    }

    public String fetchTitle(String url) {
        try {
            Document document = Jsoup.connect(url).get();
            return document.title();
        } catch (IOException e) {
            return null;
        }
    }

    public List<Istif> findRecentStories() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -7);
        Date date = calendar.getTime();
        return istifRepository.findByCreatedAtAfterOrderByIdDesc(date);
    }

    public List<IstifListResponse> findRecommendedStories(){
        List<Istif> istifList = findRecentStories();
        istifList.sort(Comparator.comparingInt(Istif::getLikesSize).reversed());
        List<IstifListResponse> istifResponseList = new ArrayList<>();
        for(Istif istif: istifList){
            istifResponseList.add(new IstifListResponse(istif));
        }
        return istifResponseList;
    }

    public List<IstifListResponse> findAllByShareFlagOrderByIdDesc(Integer shareFlag) {
        List<Istif> istifList = istifRepository.findAllByShareFlagOrderByIdDesc(shareFlag);
        List<IstifListResponse> istifResponseList = new ArrayList<>();
        for(Istif istif: istifList){
            istifResponseList.add(new IstifListResponse(istif));
        }
        return istifResponseList;
    }

    public List<IstifListResponse> searchIstifs(String query,String startDate,String endDate) throws ParseException {
        Set<Istif> istifSet = new HashSet<>();
        if(query != null && !query.isEmpty() && !query.isBlank()){
            istifSet.addAll(searchIstifsWithQuery(query));
        }
        if(startDate != null && !startDate.isBlank() && !startDate.isEmpty()){
            istifSet.addAll(searchIstifsWithDate(startDate));
            if(endDate != null && !endDate.isBlank() && !endDate.isEmpty()){
                istifSet.addAll(searchIstifsWithMultipleDate(startDate,endDate));
            }
        }
        List<Istif> publicIstifList = istifRepository.findAllByShareFlagOrderByIdDesc(1);
        istifSet.retainAll(publicIstifList);
        if(istifSet.isEmpty()){
            List<IstifListResponse> istifListResponse = new ArrayList<>();
            istifListResponse.add(new IstifListResponse());
            return istifListResponse;
        }
        return istifListToIstifListResponse(istifSet.stream().toList());
    }

    public List<IstifListResponse> istifListToIstifListResponse(List<Istif> istifList){
        List<IstifListResponse> istifListResponse = new ArrayList<>();
        for(Istif istif: istifList){
            istifListResponse.add(new IstifListResponse(istif));
        }
        return istifListResponse;
    }

    public List<Istif> shareFlagCheck(List<Istif> istifList){
        istifList.removeIf(istif -> istif.getShareFlag() != 1);
        return istifList;
    }
}
