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

@WebServlet("/BoardModifyServlet")
public class BoardModifyServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html; charset=UTF-8");
		request.setCharacterEncoding("UTF-8");
		
		String uploadFilePath = "C:/Users/user/Desktop/uploadfile";  //직접 경로설정
	    int maxSize =1024 *1024 *10;

	    MultipartRequest multi =null;
		Connection conn = null;
		

		try {
			Context initCtx = new InitialContext(); // InitialContext 생성자 만들기
			Context envCtx = (Context) initCtx.lookup("java:comp/env"); // lookup =메소드이름-뭔가를 찾는다. / "java:comp/env"는
																		// 자바등록정보를 찾을 수 있는 JNDI트리의 노드
			DataSource ds = (DataSource) envCtx.lookup("jdbc/Oracle11g");
			conn = ds.getConnection();
			multi = new MultipartRequest(request, uploadFilePath, maxSize, "utf-8", new IndexFileRenamePolicy(9999));

			listModify(multi, conn);

		} catch (Exception e) {
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

	private void listModify( MultipartRequest multi,Connection conn) throws Exception{
		
		int num=0;
	    String title ="";
	    String contents="";
	    String modFile="";
	    String modTextFileName="";
 	    
		PreparedStatement pstmtNonFileMod = null;
		PreparedStatement pstmtFileMod = null;
		ResultSet rs = null;
		

		try {
			Enumeration<?> modStepFile = multi.getFileNames(); //각각의 객체들을 한순간에 하나씩 처리 할수잇는 메소드를 제공  = 전송한 전체 파일이름들을 가져옴
	        if(modStepFile.hasMoreElements()) { //hasMoreElements() 읽어올 요소가 남아있는지 확인 
	        	String modRealFile = (String)modStepFile.nextElement();
	        	modFile = multi.getFilesystemName(modRealFile);  // 사용자가 지정해서 실제로 업로드된 파일명을 반환
	        	modTextFileName=multi.getOriginalFileName(modRealFile); // 원래 파일명을 반환 파일명 변경전
	        	
	        	if(modFile == null) {  //파일을 수정해주지 않았을경우
	        		pstmtNonFileMod = conn.prepareStatement("update Screen set title=?,contents=? where num=?");
	        		num = Integer.parseInt(multi.getParameter("number"));
	        		title = multi.getParameter("detail_title1");
	        		contents = multi.getParameter("detail_contents1");
	        		
	        		pstmtNonFileMod.setString(1, title);  
	        		pstmtNonFileMod.setString(2, contents); 
	        		pstmtNonFileMod.setInt(3, num);
	        		pstmtNonFileMod.executeUpdate();
	        	} else {  //파일까지 같이 수정해줬을 경우
	    			pstmtFileMod = conn.prepareStatement("update Screen set title=?,contents=?,file1=? where num=?");
	    			num = Integer.parseInt(multi.getParameter("number"));
	    			title = multi.getParameter("detail_title1");
	    			contents = multi.getParameter("detail_contents1");
	    			modFile = multi.getParameter("modFile");   //file3
	    			
	    			pstmtFileMod.setString(1, title);  
	    			pstmtFileMod.setString(2, contents); 
	    			pstmtFileMod.setString(3, modTextFileName);
	    			pstmtFileMod.setInt(4, num);
	    			pstmtFileMod.executeUpdate();
	    			
	    			deleteFile(num); //원래 있던 파일 삭제
	    			renameFile(modFile,num); //9999로 받은 파일명을 num으로 변경
	        	 }
	        }

		} catch (Exception e) {
			throw e;
		} finally {
			if (pstmtNonFileMod != null) {
				try {
					pstmtNonFileMod.close();
				} catch (Exception e) {
				}
			}
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
				}
			}
			if (pstmtFileMod != null) {
				try {
					pstmtFileMod.close();
				} catch (Exception e) {
				}
			}
		}

	}

	public void deleteFile(int num) throws Exception{
		File file = new File("C:/Users/user/Desktop/uploadFile/"+num); 
		if( file.exists() ){ 
			if(file.delete()){ 
				System.out.println("파일삭제 성공"); 
			}
			else{ 
				System.out.println("파일삭제 실패"); 
			} 
			
		} 
	}

	public void renameFile(String fileNew,int num) throws Exception{
		File file1 = new File("C:/Users/user/Desktop/uploadFile/9999");
		File fileNew1 = new File("C:/Users/user/Desktop/uploadFile/"+num);
		if( file1.exists() ) file1.renameTo( fileNew1 );
	}

}
