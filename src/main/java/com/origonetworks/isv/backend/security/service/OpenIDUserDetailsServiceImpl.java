package com.origonetworks.isv.backend.security.service;

import java.util.List;

import org.hibernate.ObjectNotFoundException;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Property;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.origonetworks.isv.backend.core.dao.GenericDAO;
import com.origonetworks.isv.backend.user.model.User;
import com.origonetworks.isv.backend.user.util.BeanUtils;

@Service("openIdUserDetailsService")
public class OpenIDUserDetailsServiceImpl implements UserDetailsService {
	private GenericDAO<User, Long> userDao;

	@Autowired
	public void setUserDao(GenericDAO<User, Long> userDao) {
		this.userDao = userDao;
	}

	@Override
	public UserDetails loadUserByUsername(String openId) throws UsernameNotFoundException, DataAccessException {
		DetachedCriteria criteria = DetachedCriteria.forClass(User.class).add(Property.forName("openId").eq(openId));
		List<User> result = this.userDao.findByCriteria(criteria);
		if (result == null || result.size() != 1) {
			throw new ObjectNotFoundException(openId, User.class.toString());
		} else {
			return BeanUtils.createBean(result.get(0));
		}
	}
}
