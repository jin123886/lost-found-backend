package com.example.lost_found_backend.mapper;

import com.example.lost_found_backend.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户 Mapper
 */
@Mapper
public interface UserMapper {

    User findByOpenid(@Param("openid") String openid);

    int insert(User user);

    int updateById(User user);
}
