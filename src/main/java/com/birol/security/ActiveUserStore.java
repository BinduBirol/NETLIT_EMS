package com.birol.security;

import java.util.ArrayList;
import java.util.List;

import com.birol.ems.dto.LoggedinUserDTO;


public class ActiveUserStore {

    public List<String> users;
    
    public List<LoggedinUserDTO> loggedusers;

    public ActiveUserStore() {
        users = new ArrayList<>();
        loggedusers= new ArrayList<LoggedinUserDTO>();
    }

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }

	public List<LoggedinUserDTO> getLoggedusers() {
		return loggedusers;
	}

	public void setLoggedusers(List<LoggedinUserDTO> loggedusers) {
		this.loggedusers = loggedusers;
	}
    
    
}
