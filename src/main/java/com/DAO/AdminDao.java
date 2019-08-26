package com.DAO;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import com.Bean.ApplicationBean;
import com.Bean.ClgadminBean;
import com.Bean.LoginBean;
import com.Bean.Selected_studentsBean;
import com.Bean.SubadBean;

public class AdminDao {

	EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("Student_admission");
	EntityManager entityManager = entityManagerFactory.createEntityManager();

	public String insert(ClgadminBean clgbean) {

		try
		{
		entityManager.getTransaction().begin();
		clgbean.getClgcode();
		clgbean.getClgname();
		clgbean.getAdminname();
		entityManager.persist(clgbean);

		SubadBean subbean = new SubadBean();
		subbean.getName();
		subbean.getPassword();
		subbean.getClgcode();
		subbean.setName(clgbean.getAdminname());
		subbean.setPassword(clgbean.getClgcode() + clgbean.getClgname());
		subbean.setClgcode(clgbean.getClgcode());
		entityManager.persist(subbean);

		LoginBean loginbean = new LoginBean();
		loginbean.getName();
		loginbean.getPassword();
		loginbean.getRole();
		loginbean.setName(clgbean.getAdminname());
		loginbean.setPassword(clgbean.getClgcode() + clgbean.getClgname());
		loginbean.setRole("subadmin");
		entityManager.persist(loginbean);
		entityManager.getTransaction().commit();


		}
		catch(Exception e)
		{
			e.getStackTrace();
		}
		finally {
			
		entityManager.close();
		entityManagerFactory.close();

		}
		return "admin added successfully";

	}

	public ArrayList<ClgadminBean> review_admins() {
		try {

		entityManager.getTransaction().begin();
		TypedQuery<ClgadminBean> query = entityManager.createQuery("SELECT c FROM ClgadminBean c", ClgadminBean.class);
		ArrayList<ClgadminBean> results = (ArrayList<ClgadminBean>) query.getResultList();
		
		entityManager.getTransaction().commit();
		return results;

		}
		catch(Exception e)
		{
			e.getStackTrace();
		}
		finally
		{
		entityManager.close();
		entityManagerFactory.close();

		}
		return null;

	}

	public ArrayList<ApplicationBean> review_applications(String code) {

		try
		{
		entityManager.getTransaction().begin();
		String clg_name = entityManager.find(ClgadminBean.class, code).getClgname();
		TypedQuery<ApplicationBean> query = entityManager.createQuery(
				"SELECT c FROM ApplicationBean c where college_ch1='" + clg_name + "'", ApplicationBean.class);
		ArrayList<ApplicationBean> results = (ArrayList<ApplicationBean>) query.getResultList();
		return results;

		}
		catch(Exception e )
		{
			e.getStackTrace();
		}
		finally
		{
			
		entityManager.getTransaction().commit();
		entityManager.close();
		entityManagerFactory.close();
		}
		return null;

	}

	public ArrayList<Selected_studentsBean> selected_ofchoice(String code) {
		try {
			
		entityManager.getTransaction().begin();
		String clg_name = entityManager.find(ClgadminBean.class, code).getClgname();
		TypedQuery<Selected_studentsBean> query = entityManager.createQuery(
				"select e from Selected_studentsBean  e where e.dept_choice=e.alloted_dept where college_ch1='"
						+ clg_name + "'",
				Selected_studentsBean.class);
		ArrayList<Selected_studentsBean> results = (ArrayList<Selected_studentsBean>) query.getResultList();
		return results;

		}
		catch(Exception e )
		{
			e.getStackTrace();
		}
		finally
		{
		entityManager.getTransaction().commit();
		entityManager.close();
		entityManagerFactory.close();
		}
		return null;

	}

	public ArrayList<ApplicationBean> confirm(String code) {

		try
		{
		entityManager.getTransaction().begin();
		String clg_name = entityManager.find(ClgadminBean.class, code).getClgname();
		TypedQuery<ApplicationBean> query = entityManager.createQuery(
				"SELECT c FROM ApplicationBean c where status='pending' and college_ch1='" + clg_name + "'", ApplicationBean.class);
		ArrayList<ApplicationBean> results = (ArrayList<ApplicationBean>) query.getResultList();
		return results;

		}
		catch(Exception e )
		{
			e.getStackTrace();
		}
		finally
		{
		entityManager.getTransaction().commit();
		entityManager.close();
		entityManagerFactory.close();
		}
		return null;

	}

	public String accept(String applicant_no, String code) {
		String name = entityManager.find(ApplicationBean.class, Integer.parseInt(applicant_no)).getCollege_ch1();
		List<Object> val = entityManager
				.createNativeQuery(
						"select count(applicant_no) from Selected_studentsBean where alloted_college='" + name + "';")
				.getResultList();
		System.out.println(val + "college availability");// System.out.println(code);
		BigInteger value = (BigInteger) val.get(0);
		int valu = value.intValue();

		if (valu < 26) {
			entityManager.getTransaction().begin();
			String clg_name = entityManager.find(ClgadminBean.class, code).getClgname();

			ApplicationBean bean = entityManager.find(ApplicationBean.class, Integer.parseInt(applicant_no));

			Selected_studentsBean selected_studentsbean = new Selected_studentsBean();

			selected_studentsbean.setApplicant_no(bean.getApplicant_no());
			selected_studentsbean.setName(bean.getName());
			selected_studentsbean.setPercentage(bean.getPercentage());
			selected_studentsbean.setAlloted_dept(bean.getDept_choice());
			selected_studentsbean.setAlloted_college(bean.getCollege_ch1());

			entityManager.persist(selected_studentsbean);
			ApplicationBean applicationbean = entityManager.find(ApplicationBean.class, Integer.parseInt(applicant_no));
			applicationbean.setStatus("accepted");
			entityManager.getTransaction().commit();

			entityManager.close();
			entityManagerFactory.close();

			return "student  added into selected_students successfully";
		} else {
			String result = new AdminDao().reject(applicant_no, code);

			return result;

		}
	}

	public ArrayList<Selected_studentsBean> Stud_ofgiven_dept(String code, String dept) {

		try
		{
		entityManager.getTransaction().begin();
		String clg_name = entityManager.find(ClgadminBean.class, code).getClgname();

		System.out.println("dao");
		Query queryString = entityManager
				.createQuery("SELECT e FROM com.Bean.Selected_studentsBean e where e.alloted_dept='" + dept
						+ "' and e.alloted_college='" + clg_name + "'");
		ArrayList<Selected_studentsBean> resultList = new ArrayList<Selected_studentsBean>(queryString.getResultList());
		return resultList;

		}
		catch(Exception  e)
		{
			e.getStackTrace();
		}
		finally {
		entityManager.getTransaction().commit();
		entityManager.close();
		entityManagerFactory.close();
		}
		return null;


	}

	public ArrayList<Selected_studentsBean> selected_list(String code) {

		try {
		entityManager.getTransaction().begin();
		String clg_name = entityManager.find(ClgadminBean.class, code).getClgname();
		Query queryString = entityManager.createQuery(
				"SELECT e FROM com.Bean.Selected_studentsBean e where  e.alloted_college='" + clg_name + "'");
		ArrayList<Selected_studentsBean> resultList = new ArrayList<Selected_studentsBean>(queryString.getResultList());
		return resultList;

		}
		catch(Exception e)
		{
			e.getStackTrace();
		}
		finally {
		entityManager.getTransaction().commit();
		entityManager.close();
		entityManagerFactory.close();
		}
		return null;

	}

	public String reject(String applicant_no, String code) {
		try {
		entityManager.getTransaction().begin();
		String clg_name = entityManager.find(ClgadminBean.class, code).getClgname();

		
		ApplicationBean applicationbean = entityManager.find(ApplicationBean.class, Integer.parseInt(applicant_no));
		int count=applicationbean.getCount();
		applicationbean.setStatus("rejected");
		applicationbean.setCount(count+1);
		return "student rejected";

		}
		catch(Exception e)
		{
			e.getStackTrace();
		}
		finally {
		entityManager.getTransaction().commit();

		entityManager.close();
		entityManagerFactory.close();
		}
		return null;
	}

}
