package board_Servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

@WebServlet("/BoardDeleteServlet")
public class BoardDeleteServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
 
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html; charset=UTF-8");
		request.setCharacterEncoding("UTF-8");

		Connection conn =null; 
	
		try {
			Context initCtx = new InitialContext();
			Context envCtx = (Context) initCtx.lookup("java:comp/env"); 
			DataSource ds = (DataSource) envCtx.lookup("jdbc/Oracle11g");
			conn = ds.getConnection();
			int num = Integer.parseInt(request.getParameter("num"));
			
			listDelete(conn,num);
			
			}catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (conn != null) {
					try {
						conn.close();
					} catch (Exception e) {
					}
				}
			}
		}
		
	private void listDelete (Connection conn , int num) throws Exception{
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try { 
			pstmt = conn.prepareStatement("delete from Screen where num=?");
			pstmt.setInt(1, num);
			pstmt.executeUpdate();
			
		}catch (Exception e) {
			throw e;
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
				}
			}
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (Exception e) {
				}
			}
		}
	}
}
		
