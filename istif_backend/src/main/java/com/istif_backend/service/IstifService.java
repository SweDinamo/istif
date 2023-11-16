package com.istif_backend.service;

import com.istif_backend.model.Comment;
import com.istif_backend.model.Istif;
import com.istif_backend.model.User;
import com.istif_backend.repository.IstifRepository;
import com.istif_backend.request.IstifCreateRequest;
import com.istif_backend.request.IstifEditRequest;
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


    public List<Istif> findAllByOrderByIdDesc(){
        List<Istif> istifList = istifRepository.findByShareFlagOrderByCreatedAtDesc(1);
        return istifList;
    }

    public Istif createIstif(User foundUser, IstifCreateRequest istifCreateRequest) throws ParseException, IOException {
        Istif createdIstif = Istif.builder()
                .title(fetchTitle(istifCreateRequest.getTitle()))
                .titleLink(istifCreateRequest.getTitle())
                .labels(istifCreateRequest.getLabels())
                .text(imageService.parseAndSaveImages(istifCreateRequest.getText()))
                .user(foundUser)
                .shareFlag(istifCreateRequest.getShareFlag())
                .createdAt(new Date())
                .relevantDate(istifCreateRequest.getRelevantDate())
                .likes(new HashSet<>())
                .build();
        return istifRepository.save(createdIstif);

    }

    public List<Istif> findByUserIdOrderByIdDesc(Long userId){
        return istifRepository.findByUserIdOrderByIdDesc(userId);
    }


    public Istif getIstifByIstifId(Long id) {
        Optional<Istif> optionalIstif = istifRepository.findById(id);
        if (optionalIstif.isEmpty()) {
            throw new NoSuchElementException("Istif with id '" + id + "' not found");
        }
        return optionalIstif.get();
    }

    public List<Istif> findFollowingStories(User foundUser) {
        Set<User> followingList = foundUser.getFollowing();
        List<Long> idList = new ArrayList<>();
        List<Istif> istifList = new ArrayList<>();
        for (User user : followingList){
            idList.add(user.getId());
        }
        for(Long id : idList){
            List<Istif> userIstifList = findByUserIdAndShareFlagOrderByIdDesc(id,1);
            istifList.addAll(userIstifList);
        }

        return istifList;

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
        return istifSet;
    }

    public List<Istif> searchIstifsWithDate(String date) throws ParseException {
        Date formattedDate = stringToDate(date);
        Date startDate = setToMidnight(formattedDate);
        Date endDate = setToEndOfDay(formattedDate);
        if (date.contains("01-01")){
            endDate = setToLastDayOfYear(formattedDate);
        }
        Set <Istif> results = new HashSet<>();
        results.addAll(istifRepository.findByCreatedAtBetween(startDate,endDate));
        results.addAll(istifRepository.findByRelevantDateBetween(startDate,endDate));
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

    private Date setToLastDayOfYear(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        calendar.set(Calendar.MONTH, Calendar.DECEMBER);
        calendar.set(Calendar.DAY_OF_MONTH, 31);

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

    public Date stringToDate(String timeStamp) throws ParseException{
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return dateFormat.parse(timeStamp);
        } catch (ParseException e) {
            dateFormat = new SimpleDateFormat("yyyy-01-01");
            String yearStamp = timeStamp.concat("-01-01");
            return dateFormat.parse(yearStamp);
        }
    }

    public Istif enterIstif(User foundUser, IstifEditRequest istifEditRequest) throws ParseException, IOException {
        return Istif.builder()
                .title(fetchTitle(istifEditRequest.getTitle()))
                .titleLink(istifEditRequest.getTitle())
                .labels(istifEditRequest.getLabels())
                .text(imageService.parseAndSaveImages(istifEditRequest.getText()))
                .user(foundUser)
                .createdAt(new Date())
                .likes(new HashSet<>())
                .build();
    }

    public Istif editIstif(IstifEditRequest request, User user, Long istifId) throws ParseException, IOException {
        Istif istif = getIstifByIstifId(istifId);
        if(Objects.equals(istif.getUser().getId(),user.getId())){
            Istif enteredIstif = enterIstif(user,request);
            enteredIstif.setId(istifId);
            enteredIstif.setLikes(istif.getLikes());
            enteredIstif.setId(istif.getId());
            enteredIstif.setComments(istif.getComments());
            enteredIstif.setShareFlag(istif.getShareFlag());
            return istifRepository.save(enteredIstif);
        }
        return null;
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

    public List<Istif> findByUserIdAndShareFlagOrderByIdDesc(Long userId, int shareFlag){
        return istifRepository.findByUserIdAndShareFlagOrderByIdDesc(userId,shareFlag);
    }

    public String fetchTitle(String url) {
        try {
            Document document = Jsoup.connect(url).get();
            return document.title();
        } catch (IOException e) {
            return null;
        }
    }
}
