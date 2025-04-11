package com.example.controller;

import org.springframework.web.bind.annotation.RestController;

import com.example.feign.FollowFeignClient;
import com.example.hizone.dao.follow.Follow;
import com.example.hizone.dao.user.User;
import com.example.hizone.dao.user.UserMetadata;
import com.example.hizone.front.user.UpdateUserInfo;
import com.example.hizone.front.user.UpdateUserAvatar;
import com.example.hizone.inter.UpdateUserMetadata;
import com.example.hizone.outer.UserDetail;
import com.example.hizone.outer.UserInfo;
import com.example.hizone.utility.Utility;
import com.example.service.CacheService;
import com.example.service.UserService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.seata.spring.annotation.GlobalTransactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

@CrossOrigin
@RequestMapping("/user")
@GlobalTransactional
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private FollowFeignClient followFeignClient;

    @Autowired
    private CacheService cacheService;

    /**
     * 获取用户基本信息
     * 高频高精数据-使用缓存并且数据更新时总是更新缓存
     * 
     * @param token
     * @param userId
     * @return
     */
    @GetMapping("/getUserInfoList")
    public List<UserInfo> getUserInfoList(@RequestParam("user_id_list") int[] userIdList) {
        List<UserInfo> userInfoList = new ArrayList<>();
        for (int userId : userIdList) {
            UserDetail userDetail = (UserDetail) cacheService.getCache("user" + userId);
            if (userDetail == null) {
                userDetail = userService.getUserDetail(userId);
                cacheService.setCache("user" + userId, userDetail);
            }
            UserInfo userInfo = new UserInfo();
            userInfo.setUserId(userId);
            userInfo.setNickname(userDetail.getNickname());
            userInfoList.add(userInfo);
        }
        return userInfoList;
    }

    /**
     * 获取用户详细信息
     * 低频高精数据-不缓存
     * 
     * @param userId
     * @return
     */
    @GetMapping("/getUserDetail")
    public UserDetail getUserDetail(@RequestHeader(value = "Token", required = false) String token, @RequestParam("user_id") int userId) {
        int selfId = token == null ? -1 : Utility.extractUserId(token);
        UserDetail userDetail = (UserDetail) cacheService.getCache("user" + userId);
        if (userDetail == null) {
            userDetail = userService.getUserDetail(userId);
            cacheService.setCache("user" + userId, userDetail);
        }
        userDetail.setFollowed(followFeignClient.hasFollow(new Follow(selfId, userId)));
        return userDetail;
    }

    @GetMapping("/getUserAvatar")
    public ResponseEntity<Resource> getUserAvatar(@RequestParam("user_id") int userId) throws IOException {
        File imageFile = new File("services/hizone-user/src/main/resources/avatar/" + userId + ".png");
        System.out.println(imageFile.getAbsolutePath());
        if (!imageFile.exists()) {
            System.out.println("Image file not found: " + imageFile.getAbsolutePath());
            return ResponseEntity.notFound().build(); // 如果文件不存在，返回 404
        }
        Resource imageResource = new FileSystemResource(imageFile);
        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(imageResource);
    }

    /**
     * 获取所有用户信息用于搜索
     * 低频高精数据-不缓存
     * 
     * @return
     */
    @GetMapping("/getAllUser")
    public List<User> getAllUser() {
        return userService.getAllUser();
    }

    /**
     * 高频高精数据-缓存并总是更新缓存
     * 
     * @param updateUserInfo
     * @return
     */
    @PostMapping("/updateUserInfo")
    public String updateUserInfo(@RequestBody UpdateUserInfo updateUserInfo) {
        userService.updateUserInfo(updateUserInfo);
        cacheService.deleteCache("user" + updateUserInfo.getUserId());
        return "success";
    }

    @PostMapping("/updateUserAvatar")
    public String updateUserAvatar(@RequestBody UpdateUserAvatar updateUserAvatar) throws IOException {
        Path imageFile = Paths
                .get("services/fenta-user/src/main/resources/avatar/" + updateUserAvatar.getUserId() + ".jpg");
        if (Files.exists(imageFile)) {
            Files.delete(imageFile);
        }
        updateUserAvatar.getAvatar().transferTo(imageFile);
        return "success";
    }

    /**
     * 更新用户元数据
     * 低频高精数据-不缓存
     * 
     * @param updateUserMetadata
     * @return
     */
    @PostMapping("/updateUserMetadata")
    public String updateUserMetadata(@RequestBody UpdateUserMetadata updateUserMetadata) {
        System.out.println("updateUserMetadata" + updateUserMetadata.getUserId() + " " + updateUserMetadata.getPostCount());
        UserMetadata userMetadata = userService.getUserMetadata(updateUserMetadata.getUserId());
        userMetadata.merge(updateUserMetadata);
        userService.updateUserMetadata(updateUserMetadata);
        cacheService.deleteCache("user" + updateUserMetadata.getUserId());
        return "success";
    }
}
