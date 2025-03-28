package com.example.controller;

import org.springframework.web.bind.annotation.RestController;

import com.example.fenta.dao.user.User;
import com.example.fenta.dao.user.UserDetail;
import com.example.fenta.dao.user.UserMetadata;
import com.example.fenta.front.user.UpdateUserAvatar;
import com.example.fenta.front.user.UpdateUser;
import com.example.fenta.inter.UpdateUserMetadata;
import com.example.service.CacheService;
import com.example.service.UserService;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

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

@Slf4j
@CrossOrigin
@RestController
public class UserController {

    @Autowired
    private UserService userService;

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
    @GetMapping("/getUser")
    public User getUser(@RequestParam("user_id") int userId) {
        User user = (User) cacheService.getCache("user" + userId);
        if (user != null) {
            return user;
        }
        user = userService.getUser(userId);
        cacheService.setCache("user" + userId, user);
        return user;
    }

    /**
     * 获取用户详细信息
     * 低频高精数据-不缓存
     * 
     * @param userId
     * @return
     */
    @GetMapping("/getUserDetail")
    public UserDetail getUserDetail(@RequestParam("user_id") int userId) {
        return userService.getUserDetail(userId);
    }

    @GetMapping("/getUserAvatar")
    public ResponseEntity<Resource> getUserAvatar(@RequestParam("user_id") int userId) throws IOException {
        File imageFile = new File("services/fenta-user/src/main/resources/avatar/" + userId + ".jpg");
        if (!imageFile.exists()) {
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
     * @param updateUser
     * @return
     */
    @PostMapping("/updateUser")
    public String updateUser(@RequestBody UpdateUser updateUser) {
        userService.updateUser(updateUser);
        cacheService.deleteCache("user" + updateUser.getUserId());
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
        UserMetadata userMetadata = userService.getUserMetadata(updateUserMetadata.getUserId());
        userMetadata.merge(updateUserMetadata);
        userService.updateUserMetadata(updateUserMetadata);
        return "success";
    }
}
