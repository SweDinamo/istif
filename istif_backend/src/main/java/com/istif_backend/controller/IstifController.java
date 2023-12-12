package com.istif_backend.controller;

import com.istif_backend.model.Istif;
import com.istif_backend.model.User;
import com.istif_backend.request.IstifCreateRequest;
import com.istif_backend.request.IstifEditRequest;
import com.istif_backend.request.LikeRequest;
import com.istif_backend.response.IstifListResponse;
import com.istif_backend.response.SingleIstifResponse;
import com.istif_backend.service.IstifService;
import com.istif_backend.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@RestController
@RequestMapping("/api/istif")
public class IstifController {

    @Autowired
    IstifService istifService;
    @Autowired
    private UserService userService;

    @GetMapping("/all")
    public ResponseEntity<?> findAllIstifs(HttpServletRequest request){
        return ResponseEntity.ok(istifService.findAllByShareFlagOrderByIdDesc(1));
    }

    @GetMapping("/recommended")
    public ResponseEntity<?> findFeedIstifs(HttpServletRequest request){
        return ResponseEntity.ok(istifService.findRecommendedStories());
    }

    @PostMapping("/add")
    @CrossOrigin
    public ResponseEntity<?> addIstif(@RequestBody IstifCreateRequest istifCreateRequest, HttpServletRequest request) throws ParseException, IOException {
        User user = userService.validateTokenizedUser(request);
        return ResponseEntity.ok(istifService.createIstif(user,istifCreateRequest));
    }

    @GetMapping("/fromUser")
    public ResponseEntity<?> findAllIstifsfromUser(HttpServletRequest request){
        User user = userService.validateTokenizedUser(request);
        return ResponseEntity.ok(istifService.findByUserIdOrderByIdDesc(user.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getIstifById(@PathVariable Long id,HttpServletRequest request){
        SingleIstifResponse foundIstif = istifService.retrieveIstif(id);
        if (foundIstif!=null) {
            return ResponseEntity.ok(foundIstif);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/following")
    public ResponseEntity<?> findAllIstifsfromFollowings(HttpServletRequest request){
        User tokenizedUser = userService.validateTokenizedUser(request);
        return ResponseEntity.ok(istifService.findFollowingStories(tokenizedUser));
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchIstifs(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) throws ParseException {
        List<IstifListResponse> istifList = istifService.searchIstifs(query,startDate,endDate);
        IstifListResponse emptyResponse = new IstifListResponse();
        return ResponseEntity.ok(Objects.requireNonNullElse(istifList, "No istifs with this search is found!"));
    }
    @PostMapping("/like")
    public ResponseEntity<?> likeIstif(@RequestBody LikeRequest likeRequest, HttpServletRequest request){
        User tokenizedUser = userService.validateTokenizedUser(request);
        return ResponseEntity.ok(istifService.likeIstif(likeRequest.getLikedEntityId(),tokenizedUser.getId()));
    }
    @GetMapping("/delete/{istifId}")
    public ResponseEntity<?> deleteIstif(@PathVariable Long istifId, HttpServletRequest request) {
        User tokenizedUser = userService.validateTokenizedUser(request);
        return ResponseEntity.ok(istifService.deleteByIstifId(istifService.getIstifByIstifId(istifId)));

    }

    @PostMapping("/edit/{istifId}")
    public ResponseEntity<?> editIstif(@PathVariable Long istifId, @RequestBody IstifEditRequest istifEditRequest, HttpServletRequest request) throws ParseException, IOException {
        User tokenizedUser = userService.validateTokenizedUser(request);
        return ResponseEntity.ok(istifService.editIstif(istifEditRequest,tokenizedUser,istifId));

    }

    @GetMapping("/liked")
    public ResponseEntity<?> likedIstifs(HttpServletRequest request){
        User tokenizedUser = userService.validateTokenizedUser(request);
        return ResponseEntity.ok(istifService.likedStories(tokenizedUser));
    }
}
