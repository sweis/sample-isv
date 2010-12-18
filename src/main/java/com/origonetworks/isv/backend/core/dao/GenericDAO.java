package com.origonetworks.isv.backend.core.dao;

import java.io.Serializable;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;

public interface GenericDAO<T, PK extends Serializable> {
	public void saveOrUpdate(T entity);

	public void delete(T entity);

	public T findById(PK id);

	public List<T> findAll();

	public List<T> findByCriteria(DetachedCriteria criteria);
}
