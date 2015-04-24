/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.gmailtest.dao;

import com.mycompany.gmailtest.dto.Message;
import com.mycompany.gmailtest.util.HibernateUtil;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 *
 * @author Anatolii
 */
public class MessageDao {

    public void addMessages(List<Message> messages) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        for (int i = 0; i < messages.size(); i++) {
            Message message = messages.get(i);
            session.save(message);
            if (i % 100 == 0) {
                session.flush();
                session.clear();
            }
        }
        tx.commit();
        session.close();
    }

    public List<Message> getMessages() {
        List<Message> messages = new ArrayList<>();
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Transaction tx = session.beginTransaction();
            messages = session.createCriteria(Message.class).list();
            tx.commit();
            session.close();
        } catch (Exception e) {
            throw e;
        }
        return messages;
    }
    
    public void deleteMessages() {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Transaction tx = session.beginTransaction();
            session.createQuery("DELETE FROM Message").executeUpdate();
            tx.commit();
            session.close();
        } catch (Exception e) {
            throw e;
        }
    }
}
