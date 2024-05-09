package com.istif_backend.service;

import com.istif_backend.model.Istif;
import com.istif_backend.model.User;
import com.istif_backend.repository.IstifRepository;
import com.istif_backend.request.IstifCreateRequest;
import com.istif_backend.response.IstifListResponse;
import com.istif_backend.response.SingleIstifResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class IstifServiceTest {

    @Mock
    private IstifRepository istifRepository;

    @Mock
    private UserService userService;

    @Mock
    private ImageService imageService;

    @InjectMocks
    private IstifService istifService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    public void testFindAllByOrderByIdDesc() {
        List<Istif> mockIstifs = Arrays.asList(new Istif(), new Istif()); // Create mock Istif objects
        when(istifRepository.findByShareFlagOrderByCreatedAtDesc(1)).thenReturn(mockIstifs);

        List<IstifListResponse> responses = istifService.findAllByOrderByIdDesc();

        assertNotNull(responses);
        assertEquals(2, responses.size());
        verify(istifRepository, times(1)).findByShareFlagOrderByCreatedAtDesc(1);
    }


    @Test
    public void testCreateIstif() throws ParseException, IOException {
        User user = User.builder()
                .username("testUser")
                .build();
        IstifCreateRequest request = new IstifCreateRequest();
        request.setTitleLink("https://www.test.com/");
        request.setText("text");
        request.setTitle("title");
        request.setShareFlag(1);
        Istif mockIstif = Istif.builder()
                .title("title")
                .source("source")
                .titleLink("https://www.test.com/")
                .DateFlag(0)
                .shareFlag(1)
                .user(user)
                .build();
        when(istifRepository.save(any(Istif.class))).thenReturn(mockIstif);

        Istif createdIstif = istifService.createIstif(user, request);

        assertNotNull(createdIstif);
        verify(imageService, times(1)).parseAndSaveImages(anyString());
        verify(istifRepository, times(1)).save(any(Istif.class));
    }

    @Test
    public void testGetIstifByIstifId() {
        Long id = 1L;
        Istif mockIstif = new Istif();
        Optional<Istif> optionalIstif = Optional.of(mockIstif);

        when(istifRepository.findById(id)).thenReturn(optionalIstif);

        Istif result = istifService.getIstifByIstifId(id);

        assertNotNull(result);
        verify(istifRepository, times(1)).findById(id);
    }

    @Test
    public void testGetIstifByIstifId_NotFound() {
        Long id = 1L;
        when(istifRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> {
            istifService.getIstifByIstifId(id);
        });
    }


    @Test
    public void testFindByUserIdOrderByIdDesc() {
        Long userId = 1L;
        List<Istif> mockIstifs = Arrays.asList(new Istif(), new Istif());
        when(istifRepository.findByUserIdOrderByIdDesc(userId)).thenReturn(mockIstifs);

        List<IstifListResponse> responses = istifService.findByUserIdOrderByIdDesc(userId);

        assertNotNull(responses);
        assertEquals(2, responses.size());
        verify(istifRepository, times(1)).findByUserIdOrderByIdDesc(userId);
    }


    @Test
    public void testRetrieveIstif() {
        Long istifId = 1L;
        Istif mockIstif = new Istif();
        when(istifRepository.findById(istifId)).thenReturn(Optional.of(mockIstif));

        SingleIstifResponse response = istifService.retrieveIstif(istifId);

        assertNotNull(response);
        verify(istifRepository, times(1)).findById(istifId);
    }

    @Test
    public void testFindFollowingStories() {
        User user = User.builder()
                .username("testUser")
                .build();
        user.setId(1L);
        User user2 = User.builder()
                .username("testUser2")
                .build();
        user2.setId(2L);
        User user3 = User.builder()
                .username("testUser2")
                .build();
        user3.setId(2L);
        IstifCreateRequest request = new IstifCreateRequest();
        request.setTitleLink("https://www.test.com/");
        request.setText("text");
        request.setTitle("title");
        request.setShareFlag(1);
        Istif mockIstif = Istif.builder()
                .title("title")
                .source("source")
                .titleLink("https://www.test.com/")
                .DateFlag(0)
                .shareFlag(1)
                .user(user3)
                .build();

        Istif mockIstif2 = Istif.builder()
                .title("title")
                .source("source")
                .titleLink("https://www.test.com/")
                .DateFlag(0)
                .shareFlag(1)
                .user(user2)
                .build(); // setup user with following list
        Set<User> followingUsers = new HashSet<>();
        followingUsers.add(user3);
        followingUsers.add(user2);
        user.setFollowing(followingUsers);

        when(istifRepository.findByUserIdAndShareFlagOrderByIdDesc(anyLong(), eq(1)))
                .thenReturn(Arrays.asList(mockIstif, mockIstif2));

        List<IstifListResponse> responses = istifService.findFollowingStories(user);

        assertNotNull(responses);
        assertEquals(4, responses.size()); // Assuming each user has 2 stories
        verify(istifRepository, times(2)).findByUserIdAndShareFlagOrderByIdDesc(anyLong(), eq(1));
    }


    @Test
    public void testLikeIstif() {
        Long istifId = 1L, userId = 1L;
        Istif mockIstif = new Istif();
        User mockUser = new User();
        Set<Long> likes = new HashSet<>();

        when(istifRepository.findById(istifId)).thenReturn(Optional.of(mockIstif));
        when(userService.findByUserId(userId)).thenReturn(mockUser);
        when(istifRepository.save(any(Istif.class))).thenReturn(mockIstif);

        Istif istif = istifService.likeIstif(istifId, userId);

        assertNotNull(istif);
        verify(istifRepository, times(1)).save(any(Istif.class));
    }


    @Test
    public void testSearchIstifsWithQuery() {
        String query = "test";
        User user = User.builder()
                .username("testUser")
                .build();
        Istif mockIstif = Istif.builder()
                .title("test")
                .source("source")
                .titleLink("https://www.test.com/")
                .DateFlag(0)
                .shareFlag(1)
                .user(user)
                .build();

        Istif mockIstif2 = Istif.builder()
                .title("test")
                .source("source")
                .titleLink("https://www.test.com/")
                .DateFlag(0)
                .shareFlag(1)
                .user(user)
                .build();
        Set<Istif> mockIstifs = new HashSet<>(Arrays.asList(mockIstif, mockIstif2));

        when(istifRepository.findByTitleContainingIgnoreCase(query)).thenReturn(mockIstifs.stream().toList());
        when(istifRepository.findByLabelsContainingIgnoreCase(query)).thenReturn(mockIstifs.stream().toList());
        when(istifRepository.findBySourceContainingIgnoreCase(query)).thenReturn(mockIstifs.stream().toList());

        Set<Istif> istifs = istifService.searchIstifsWithQuery(query);

        assertNotNull(istifs);
        assertEquals(2, istifs.size()); // assuming no duplicates
        verify(istifRepository, times(1)).findByTitleContainingIgnoreCase(query);
        verify(istifRepository, times(1)).findByLabelsContainingIgnoreCase(query);
        verify(istifRepository, times(1)).findBySourceContainingIgnoreCase(query);
    }
}
