package com.birol.ems.datatable;

import java.util.ArrayList;

import com.birol.ems.timereport.re.Time_Report_DTO;

public class PendingTRDataTable {
	int draw;
	int recordsTotal;
	int recordsFiltered;
	
	ArrayList<Time_Report_DTO> data;

	public ArrayList<Time_Report_DTO> getData() {
		return data;
	}

	public void setData(ArrayList<Time_Report_DTO> data) {
		this.data = data;
	}

	public int getDraw() {
		return draw;
	}

	public void setDraw(int draw) {
		this.draw = draw;
	}

	public int getRecordsTotal() {
		return recordsTotal;
	}

	public void setRecordsTotal(int recordsTotal) {
		this.recordsTotal = recordsTotal;
	}

	public int getRecordsFiltered() {
		return recordsFiltered;
	}

	public void setRecordsFiltered(int recordsFiltered) {
		this.recordsFiltered = recordsFiltered;
	}
	
	

}
