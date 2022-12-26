package com.gemini.LMS.service;

import com.gemini.LMS.dao.UserDao;
import com.gemini.LMS.model.UserInfo;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UserDetailServiceImpl implements UserDetailsService {
    private final UserDao userDao;

    public UserDetailServiceImpl(final UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserInfo user = userDao.getUserDetails(username);
        return UserDetailsImpl.build(user);
    }
}
