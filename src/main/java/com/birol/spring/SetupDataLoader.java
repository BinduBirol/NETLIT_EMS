package com.birol.spring;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.birol.ems.dto.EMPLOYEE_BASIC;
import com.birol.ems.repo.EmployeeRepository;
import com.birol.ems.timereport.dto.Availability_Type;
import com.birol.ems.timereport.dto.Overtime_Type;
import com.birol.ems.timereport.repo.AvailabilityRepo;
import com.birol.ems.timereport.repo.OvertimeRepo;
import com.birol.persistence.dao.PrivilegeRepository;
import com.birol.persistence.dao.RoleRepository;
import com.birol.persistence.dao.UserRepository;
import com.birol.persistence.model.Privilege;
import com.birol.persistence.model.Role;
import com.birol.persistence.model.User;

@Component
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {

    private boolean alreadySetup = false;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PrivilegeRepository privilegeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private EmployeeRepository employeeRepository;
    
    @Autowired
    private OvertimeRepo overtimeRepo;
    @Autowired
    private AvailabilityRepo availabilityRepo;
    // API

    @Override
    @Transactional
    public void onApplicationEvent(final ContextRefreshedEvent event) {
        if (alreadySetup) {
            return;
        }

        // == create initial privileges
        final Privilege readPrivilege = createPrivilegeIfNotFound("READ_PRIVILEGE");
        final Privilege writePrivilege = createPrivilegeIfNotFound("WRITE_PRIVILEGE");
        final Privilege passwordPrivilege = createPrivilegeIfNotFound("CHANGE_PASSWORD_PRIVILEGE");

        // == create initial roles
        final List<Privilege> adminPrivileges = new ArrayList<>(Arrays.asList(readPrivilege, writePrivilege, passwordPrivilege));
        final List<Privilege> userPrivileges = new ArrayList<>(Arrays.asList(readPrivilege, passwordPrivilege));
        final Role adminRole = createRoleIfNotFound("SUPER ADMIN", adminPrivileges);
        createRoleIfNotFound("EMPLOYEE", userPrivileges);
        createRoleIfNotFound("HELPDESK", userPrivileges);
        createRoleIfNotFound("HR ADMIN", adminPrivileges);
        createRoleIfNotFound("SALARY ADMIN", adminPrivileges);
        createRoleIfNotFound("GROUP LEADER", adminPrivileges); 
        
        createAvailabilityTypeIfNotFound(1,"Worked Time",100);
        createAvailabilityTypeIfNotFound(3,"Sick Leave",100);
        createAvailabilityTypeIfNotFound(4,"Vacation",50);
        createAvailabilityTypeIfNotFound(5,"Child Care",90);
        createAvailabilityTypeIfNotFound(6,"Absent for other reason",0);
        
        createOvertimeTypeIfNotFound(1,"OB-1",110);
        createOvertimeTypeIfNotFound(2,"OB-2",110);
        createOvertimeTypeIfNotFound(3,"OB-3",110);
        createOvertimeTypeIfNotFound(4,"OB-4",110);
        createOvertimeTypeIfNotFound(5,"OB-5",110);

        // == create initial user
        createUserIfNotFound(1,"test@test.com", "Test", "Test", "test", new ArrayList<>(Arrays.asList(adminRole)));

        alreadySetup = true;
    }

    @Transactional
    Privilege createPrivilegeIfNotFound(final String name) {
        Privilege privilege = privilegeRepository.findByName(name);
        if (privilege == null) {
            privilege = new Privilege(name);
            privilege = privilegeRepository.save(privilege);
        }
        return privilege;
    }

    @Transactional
    Role createRoleIfNotFound(final String name, final Collection<Privilege> privileges) {
        Role role = roleRepository.findByName(name);
        if (role == null) {
            role = new Role(name);
        }
        role.setPrivileges(privileges);
        role = roleRepository.save(role);
        return role;
    }
    
    @Transactional
    Availability_Type createAvailabilityTypeIfNotFound(int typeid,final String name, int percentage) {
    	Availability_Type avt = availabilityRepo.findByTypename(name);
        if (avt == null) {
        	avt = new Availability_Type(typeid,name,percentage,true); 
        	avt.setIsactive(true);
        	avt.setPercentage(percentage);
        }
        avt = availabilityRepo.save(avt);
        return avt;
    }

    @Transactional
    Overtime_Type createOvertimeTypeIfNotFound(int typeid,final String name, int percentage) {
    	Overtime_Type ovt = overtimeRepo.findByTypename(name);
        if (ovt == null) {
        	ovt = new Overtime_Type(typeid,name,percentage,true); 
        	ovt.setIsactive(true);
        }
        ovt = overtimeRepo.save(ovt);
        return ovt;
    }
    
    @Transactional
    User createUserIfNotFound(final int id,final String email, final String firstName, final String lastName, final String password, final Collection<Role> roles) {
        User user = userRepository.findByEmail(email);      
        
        if (user == null) {
            user = new User();
            user.setId((long) id);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setPassword(passwordEncoder.encode(password));
            user.setEmail(email);
            user.setEnabled(true);
        }
        EMPLOYEE_BASIC empexists = employeeRepository.findbyEmpid((long)id);
        if(empexists==null) {
        	EMPLOYEE_BASIC emp = new EMPLOYEE_BASIC();
        	emp.setEmpid((long)id);
        	emp.setEmail(email);
        	emp.setFirst_name(firstName);
        	emp.setLast_name(lastName);
        	emp.setRoleid(4);
        	emp.setStatus(true);
        	employeeRepository.save(emp);
        }
        
        user.setRoles(roles);
        user = userRepository.save(user);
        return user;
    }

}