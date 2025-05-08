package com.example.controller;

import org.springframework.web.bind.annotation.RestController;

import com.example.feign.FollowFeignClient;
import com.example.hizone.dto.UpdateUserMetadata;
import com.example.hizone.request.user.UpdateUserAvatar;
import com.example.hizone.request.user.UpdateUserInfo;
import com.example.hizone.response.UserDetail;
import com.example.hizone.response.UserInfo;
import com.example.hizone.table.follow.Follow;
import com.example.hizone.table.user.User;
import com.example.hizone.utility.TokenUtility;
import com.example.service.UserCacheService;
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
import org.springframework.beans.factory.annotation.Value;
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
    private UserCacheService cacheService;

    @Value("${static.avatar.path}")
    private String avatarPath;

    /**
     * get user basic info by user id list
     * high frequency high accuracy data
     * use cache and always update cache when data is updated
     */
    @GetMapping("/getUserInfoList")
    public List<UserInfo> getUserInfoList(@RequestParam("user_id_list") List<Long> userIdList) {
        if (userIdList == null || userIdList.isEmpty()) {
            return new ArrayList<>();
        }
        if (userIdList.size() > 100) {
            throw new IllegalArgumentException("user id list size must be less than or equal to 100");
        }
        return userService.getUserInfoList(userIdList);
    }

    /**
     * get single user detail
     * low frequency high accuracy data
     * use cache and always update cache when data is updated
     */
    @GetMapping("/getUserDetail")
    public UserDetail getUserDetail(@RequestHeader("Token") String token, @RequestParam("user_id") Long userId) {
        Long selfId = token == null ? -1 : TokenUtility.extractUserId(token);
        return userService.getUserDetail(selfId, userId);
    }

    @GetMapping("/getUserAvatar")
    public ResponseEntity<Resource> getUserAvatar(@RequestParam("user_id") Integer userId) throws IOException {
        File imageFile = new File(avatarPath + userId + ".png");
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
        Path imageFile = Paths.get(avatarPath + updateUserAvatar.getUserId() + ".png");
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
        System.out.println("updateUserMetadata: " + updateUserMetadata);
        userService.updateUserMetadata(updateUserMetadata);
        return "success";
    }
}
