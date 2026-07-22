package com.mgt.service;

import com.mgt.dao.CustomerDAO;
import com.mgt.model.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class CustomerService {

    @Autowired
    CustomerDAO customerDAO;

    public void create(Customer customer) {
        if (customerDAO.findByEmail(customer.getEmail()) != null) {
            throw new RuntimeException("Email already registered: " + customer.getEmail());
        }
        customer.setCreatedAt(LocalDate.now());
        if (customer.getType()   == null) customer.setType("regular");
        if (customer.getStatus() == null) customer.setStatus("active");
        customerDAO.save(customer);
    }

    public List<Customer> getAll() {
        return customerDAO.getAll();
    }

    public Customer getById(long id) {
        return customerDAO.getById(id);
    }

    public void update(long id, Customer customer) {
        customer.setId(id);
        customerDAO.update(customer);
    }

    public void delete(long id) {
        customerDAO.delete(id);
    }

    public Long countTotal()              { return customerDAO.countTotal(); }
    public Long countActive()             { return customerDAO.countByStatus("active"); }
    public Long countVip()                { return customerDAO.countByType("vip"); }
}
