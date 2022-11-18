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
import com.birol.ems.timereport.re.TimeReportTypesDTO;
import com.birol.ems.timereport.re.TimeReportTypesRepo;
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
    private TimeReportTypesRepo timeReportTypesRepo;
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
        
        //String name, int percentage, String start,String end, int interval, boolean isworking, boolean isob
        createTimeReportTypeIfNotFound("Regular work time",100, "08:00","16:30",30,true,false);
        createTimeReportTypeIfNotFound("Sick Leave",0,"","",0,false,false);
        createTimeReportTypeIfNotFound("Vacation",0,"","",0,false,false);
        createTimeReportTypeIfNotFound("Child Care",0,"","",0,false,false);
        createTimeReportTypeIfNotFound("Absent for other reason",0,"","",0,false,false);
        createTimeReportTypeIfNotFound("Holiday",0,"","",0,false,false);
        
        createTimeReportTypeIfNotFound	("OB-Morning",110,"06:00","08:00",0,true,true);        
        createTimeReportTypeIfNotFound	("OB-Evening",110,"18:00","23:59",0,true,true);        
        createTimeReportTypeIfNotFound	("OB-Weekend",110,"08:00","16:30",30,true,true);        
        createTimeReportTypeIfNotFound	("OB-4",110,"00:00","06:00",0,true,true);        
        createTimeReportTypeIfNotFound	("OB-5",110,"08:00","16:30",30,true,true);
        
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
    TimeReportTypesDTO createTimeReportTypeIfNotFound(String name, int percentage, String start,String end, int interval, boolean isworking, boolean isob) {
    	
    	//TimeReportTypesDTO avt = new TimeReportTypesDTO();
    	TimeReportTypesDTO avt = timeReportTypesRepo.findByTypename(name);
        if (avt == null) {
        	avt = new TimeReportTypesDTO(name,  start,  end, interval,
        			percentage, isworking, isob);
        }
        avt = timeReportTypesRepo.save(avt);
        return avt;
        
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