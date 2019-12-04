package board_Servlet;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Enumeration;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import com.oreilly.servlet.MultipartRequest;

@WebServlet("/SaveInfoServlet")
public class SaveInfoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		
		String uploadFilePath = "C:/Users/user/Desktop/uploadfile";  //직접 경로설정
		File fileSaveDir = new File(uploadFilePath);
		System.out.println("저장경로 : " + uploadFilePath);
		int maxSize =1024 *1024 *10;
 	    
 	   	MultipartRequest multi =null;//첨부파일을 받기위한 메소드
		Connection conn = null;
		
		try {
			Context initCtx = new InitialContext();   //InitialContext 생성자 만들기
			Context envCtx = (Context) initCtx.lookup("java:comp/env");   // lookup =메소드이름-뭔가를 찾는다. / "java:comp/env"는 자바 등록정보를 찾을 수 있는 JNDI트리의 노드
			DataSource ds = (DataSource) envCtx.lookup("jdbc/Oracle11g");
			conn = ds.getConnection();
			//저장할수 있는 폴더가 없다면 만들어 줌
			if (!fileSaveDir.exists()) {
				fileSaveDir.mkdirs();
			}
			
			int changeNum=changeIndex(conn);
			multi =new MultipartRequest(request,uploadFilePath,maxSize,"utf-8",new IndexFileRenamePolicy(changeNum));
			saveInfo(conn,multi,changeNum);

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
	
	private int changeIndex (Connection conn) throws Exception {
		
	    int line=0;
	    
	    ResultSet rs = null;  //select문 실행시에 결과를 저장
		PreparedStatement pstmtIndex = null;  //SQL문을 사용할 때 setter을 이용해 값을 지정(select 문)
		
		try {
			String index = "select num.NEXTVAL as line from dual"; // as는 num.NEXTVAL의 열의 이름을 만들어줌 dual은 일회용 테이블
			pstmtIndex = conn.prepareStatement(index);
			rs = pstmtIndex.executeQuery(); // select 구문쓸때 같이 짝처럼 다녀야함
			while (rs.next()) {
				line = rs.getInt("line");
			}

		} catch (Exception e) {
			throw e;
		} finally {
			if (pstmtIndex != null) {
				try {
					pstmtIndex.close();
				} catch (Exception e) {
				}
			}
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
				}
			}
		}
		return line;
	}
	
	private void saveInfo(Connection conn,MultipartRequest multi,int line)throws Exception {
		
	    String title = null;
	    String contents = null;
 	   	String textFileName="";  //원래 파일 명
 	    
		PreparedStatement pstmtInsertContents = null;  //SQL문을 사용할 때 setter값을 이용해 값을 지정(insert 문)
		
		try {
			pstmtInsertContents = conn.prepareStatement("insert into Screen(num,title,contents,file1) values(?,?,?,?)");
			title = multi.getParameter("title");
			contents = multi.getParameter("contents");

			Enumeration<?> stepFiles = multi.getFileNames(); // 각각의 객체들을 한순간에 하나씩 처리 할수잇는 메소드를 제공 = 전송한 전체 파일이름들을 가져옴
			String realFile = (String) stepFiles.nextElement();
			textFileName = multi.getOriginalFileName(realFile);// 원래 파일명을 반환 파일명 변경전
			// insert에 ?값을 입력
			pstmtInsertContents.setInt(1, line);
			pstmtInsertContents.setString(2, title);
			pstmtInsertContents.setString(3, contents);
			pstmtInsertContents.setString(4, textFileName);
			pstmtInsertContents.executeUpdate(); // insert 구문쓸때 같이 짝처럼 다녀야함

		} catch (Exception e) {
			throw e;
		} finally {
			if (pstmtInsertContents != null) {
				try {
					pstmtInsertContents.close();
				} catch (Exception e) {
				}
			}
		}
	}	
}

