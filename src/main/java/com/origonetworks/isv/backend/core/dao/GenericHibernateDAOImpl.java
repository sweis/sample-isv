package com.origonetworks.isv.backend.core.dao;

import java.io.Serializable;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class GenericHibernateDAOImpl<T, PK extends Serializable> extends HibernateDaoSupport implements GenericDAO<T, PK> {
	private Class<T> type;

	public GenericHibernateDAOImpl(Class<T> type) {
		this.type = type;
	}

	@Override
	public void saveOrUpdate(T entity) {
		getHibernateTemplate().saveOrUpdate(entity);
	}

	@Override
	public void delete(T entity) {
		getHibernateTemplate().delete(entity);
	}

	@Override
	public T findById(PK id) {
		return getHibernateTemplate().load(type, id);
	}

	@Override
	public List<T> findAll() {
		return getHibernateTemplate().loadAll(type);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<T> findByCriteria(DetachedCriteria criteria) {
		return getHibernateTemplate().findByCriteria(criteria);
	}
}