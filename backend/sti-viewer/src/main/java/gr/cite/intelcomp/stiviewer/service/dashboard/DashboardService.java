package gr.cite.intelcomp.stiviewer.service.dashboard;

import gr.cite.intelcomp.stiviewer.model.shared.DashboardLookup;

import javax.management.InvalidApplicationException;
import javax.naming.OperationNotSupportedException;
import java.security.NoSuchAlgorithmException;

public interface DashboardService {
	String getPublicDashboard(String key) throws InvalidApplicationException;
	String getPublicDashboard(DashboardLookup lookup) throws NoSuchAlgorithmException, OperationNotSupportedException;
}
