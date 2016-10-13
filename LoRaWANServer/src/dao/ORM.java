package dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

public class ORM {

	public static void main(String[] args) {
		Configuration cfg = new Configuration();
		cfg.configure("Hibernate.cfg.xml");
		
		@SuppressWarnings("deprecation")
		SessionFactory sf = cfg.buildSessionFactory();
		Session s = sf.openSession();
		Transaction tx = s.beginTransaction();
		
		@SuppressWarnings("unchecked")
		List<Device> list = s.createCriteria(Device.class).list();
		for (Device temp : list) {
			System.out.println(temp.getId());
			System.out.println(temp.getType());
		}
		
		Device d = new Device();
		d.setId("0004000400040004");
		d.setType("IEC");
		s.save(d);
		s.flush();
		tx.commit();
		
	}

}
