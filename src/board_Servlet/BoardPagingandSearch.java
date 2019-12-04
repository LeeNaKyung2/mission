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
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import com.google.gson.Gson;

import board_Vo.BoardInfoVo;

public class BoardPagingandSearch extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html; charset=UTF-8");
		request.setCharacterEncoding("UTF-8");
		Connection conn =null; //DB를 연결해주기 위한 것
		
		int curPage = request.getParameter("curPage") == null? 1 : Integer.parseInt(request.getParameter("curPage"));  //내가 클릭한 페이지 
		String searchType= request.getParameter("searchType");
		String searchContents = request.getParameter("searchContents");

		try {
			Context initCtx = new InitialContext(); // InitialContext 생성자 만들기
			Context envCtx = (Context) initCtx.lookup("java:comp/env"); // lookup =메소드이름-뭔가를 찾는다. / "java:comp/env"는 자바등록정보를 찾을 수 있는 JNDI트리의 노드
			DataSource ds = (DataSource) envCtx.lookup("jdbc/Oracle11g");
			conn = ds.getConnection();

			int total = getTotalCount(conn, searchType, searchContents);    //게시물의 총 수와 검색했을 때의 게시물 수를 가져옴
			List<BoardInfoVo> list= getContentsList(conn, curPage, searchType, searchContents, total);
			//게시물 리스트랑 검색했을 때의 게시물 리스트를 가져옴
			
			Gson gson = new Gson();
			String jsonPlacetest = gson.toJson(list); //리스트를 Json 형태로 변경
			PrintWriter out = response.getWriter();  //쓰기를 통해 응답한다
			out.write(jsonPlacetest);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	//게시물의 총 수와 검색했을 때의 게시물 수를 가져옴
	private int getTotalCount(Connection conn, String searchType, String searchContents) throws Exception{
		int totalCount = 0; // 총 게시물 수
		PreparedStatement pstmtCount = null;
		ResultSet rsCount = null;
		StringBuffer sqlCount= new StringBuffer();
		
		try {
			sqlCount.append("select count(*) from screen ");

			if ("title".equals(searchType)) {
				sqlCount.append(" where title like ? ");
			} else if ("contents".equals(searchType)) {
				sqlCount.append(" where contents like ?  ");
			}
			sqlCount.append(" order by num desc ");

			pstmtCount = conn.prepareStatement(sqlCount.toString());

			if ("title".equals(searchType) || "contents".equals(searchType)) {
				pstmtCount.setString(1, "%" + searchContents + "%"); 
			}
			rsCount = pstmtCount.executeQuery();
			if (rsCount.next()) {
				totalCount = rsCount.getInt(1);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			if (rsCount != null) {
				try {
					rsCount.close();
				} catch (Exception e) {
				}
			}
			if (pstmtCount != null) {
				try {
					pstmtCount.close();
				} catch (Exception e) {
				}
			}
		}
		return totalCount ;
	}
	
	private List<BoardInfoVo> getContentsList(Connection conn,int curPage, String searchType, String searchContents , int totalCount)throws Exception{
		
		List<BoardInfoVo> contentsList = new ArrayList<BoardInfoVo>();
		PreparedStatement pstmtList = null; 
		ResultSet rsList = null;
		
		int totalPage = 0;
		int pageSize=10; 
		int startFileNum = (curPage-1)*10+1; //시작페이지 게시물번호
		int endFileNum = startFileNum+10-1;  //시작 페이지 게시물 끝번호
		
		StringBuffer sqlList= new StringBuffer();

		try {
			sqlList.append(" select * from ");
			sqlList.append(" (select num , title , contents , file1, ");
			sqlList.append(" ROW_NUMBER() OVER (ORDER BY num desc)as rNum ");
			sqlList.append(" from screen ");

			if ("title".equals(searchType)) {
				sqlList.append(" where title like ? ");

			} else if ("contents".equals(searchType)) {
				sqlList.append(" where contents like ? ");
			}
			sqlList.append(")A where rnum between ? and ? ");
			sqlList.append(" order by num desc ");
			pstmtList = conn.prepareStatement(sqlList.toString());

			if ("title".equals(searchType) || "contents".equals(searchType)) {
				pstmtList.setString(1, "%" + searchContents + "%");
			}
			pstmtList.setInt(2, startFileNum);
			pstmtList.setInt(3, endFileNum);
			rsList = pstmtList.executeQuery();

			totalPage = (int) Math.ceil(totalCount / (double) pageSize); // 페이지수

			while (rsList.next()) {

				BoardInfoVo boardInfo = new BoardInfoVo();
				boardInfo.setNum(rsList.getInt("num"));
				boardInfo.setTitle(rsList.getString("title"));
				boardInfo.setContents(rsList.getString("contents"));
				boardInfo.setFile1(rsList.getString("file1"));
				boardInfo.setTotal(totalPage);
				contentsList.add(boardInfo);
			}

		} catch (Exception e) {
			throw e;
		} finally {
			if (rsList != null) {
				try {
					rsList.close();
				} catch (Exception e) {
				}
			}
			if (pstmtList != null) {
				try {
					pstmtList.close();
				} catch (Exception e) {
				}
			}
		}
		return contentsList;
	}
}