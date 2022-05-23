package com.birol.web.error;

import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
import org.hibernate.exception.GenericJDBCException;
import org.springframework.dao.DataAccessException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.ModelAndView;

import com.mysql.cj.jdbc.exceptions.PacketTooBigException;

@ControllerAdvice
public class ExceptionAdvice {

	@ExceptionHandler(MaxUploadSizeExceededException.class)
	public ModelAndView handleMaxSizeException(MaxUploadSizeExceededException exc, HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView modelAndView = new ModelAndView("theme/ajaxResponse");
		modelAndView.getModel().put("message", exc.getMessage());
		modelAndView.getModel().put("class", "text-danger");
		return modelAndView;
	}

	@ExceptionHandler({ PacketTooBigException.class })
	public ModelAndView handleSQLException(PacketTooBigException exc, 
			HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView modelAndView = new ModelAndView("theme/ajaxResponse");
		modelAndView.getModel().put("message", exc.getMessage());
		modelAndView.getModel().put("class", "text-danger");
		return modelAndView;
	}

}