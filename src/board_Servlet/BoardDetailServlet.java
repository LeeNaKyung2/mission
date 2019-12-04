package board_Servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import com.google.gson.Gson;

import board_Vo.BoardInfoVo;

@WebServlet("/BoardDetailServlet")
public class BoardDetailServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html; charset=UTF-8");
		
		Connection conn = null;
		
		try {
			Context initCtx = new InitialContext(); // InitialContext 생성자 만들기
			Context envCtx = (Context) initCtx.lookup("java:comp/env"); // lookup =메소드이름-뭔가를 찾는다. / "java:comp/env"는 자바등록정보를 찾을 수 있는 JNDI트리의 노드
			DataSource ds = (DataSource) envCtx.lookup("jdbc/Oracle11g");
			conn = ds.getConnection();
			int num= Integer.parseInt(request.getParameter("num"));
			
			List<BoardInfoVo> list = detaiList(conn,num);
			
			Gson gson = new Gson();
			String jsonPlacetest = gson.toJson(list);
			PrintWriter out = response.getWriter();
			out.write(jsonPlacetest);
	
		
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
	private List<BoardInfoVo> detaiList(Connection conn, int num)throws Exception {
		
		List<BoardInfoVo> detail = new ArrayList<BoardInfoVo>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = conn.prepareStatement("select * from screen where num="+num);
			rs = pstmt.executeQuery();
		
			while (rs.next()) {

				BoardInfoVo boardInfo = new BoardInfoVo();
				boardInfo.setNum(rs.getInt("num"));
				boardInfo.setTitle(rs.getString("title"));
				boardInfo.setContents(rs.getString("contents"));
				boardInfo.setFile1(rs.getString("file1"));
				detail.add(boardInfo);
			}
		} catch (Exception e) {
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
		return detail;
	}
}
