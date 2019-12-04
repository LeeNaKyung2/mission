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
		
		String uploadFilePath = "C:/Users/user/Desktop/uploadfile";  //���� ��μ���
	    int maxSize =1024 *1024 *10;

	    MultipartRequest multi =null;
		Connection conn = null;
		

		try {
			Context initCtx = new InitialContext(); // InitialContext ������ �����
			Context envCtx = (Context) initCtx.lookup("java:comp/env"); // lookup =�޼ҵ��̸�-������ ã�´�. / "java:comp/env"��
																		// �ڹٵ�������� ã�� �� �ִ� JNDIƮ���� ���
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
			Enumeration<?> modStepFile = multi.getFileNames(); //������ ��ü���� �Ѽ����� �ϳ��� ó�� �Ҽ��մ� �޼ҵ带 ����  = ������ ��ü �����̸����� ������
	        if(modStepFile.hasMoreElements()) { //hasMoreElements() �о�� ��Ұ� �����ִ��� Ȯ�� 
	        	String modRealFile = (String)modStepFile.nextElement();
	        	modFile = multi.getFilesystemName(modRealFile);  // ����ڰ� �����ؼ� ������ ���ε�� ���ϸ��� ��ȯ
	        	modTextFileName=multi.getOriginalFileName(modRealFile); // ���� ���ϸ��� ��ȯ ���ϸ� ������
	        	
	        	if(modFile == null) {  //������ ���������� �ʾ������
	        		pstmtNonFileMod = conn.prepareStatement("update Screen set title=?,contents=? where num=?");
	        		num = Integer.parseInt(multi.getParameter("number"));
	        		title = multi.getParameter("detail_title1");
	        		contents = multi.getParameter("detail_contents1");
	        		
	        		pstmtNonFileMod.setString(1, title);  
	        		pstmtNonFileMod.setString(2, contents); 
	        		pstmtNonFileMod.setInt(3, num);
	        		pstmtNonFileMod.executeUpdate();
	        	} else {  //���ϱ��� ���� ���������� ���
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
	    			
	    			deleteFile(num); //���� �ִ� ���� ����
	    			renameFile(modFile,num); //9999�� ���� ���ϸ��� num���� ����
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
				System.out.println("���ϻ��� ����"); 
			}
			else{ 
				System.out.println("���ϻ��� ����"); 
			} 
			
		} 
	}

	public void renameFile(String fileNew,int num) throws Exception{
		File file1 = new File("C:/Users/user/Desktop/uploadFile/9999");
		File fileNew1 = new File("C:/Users/user/Desktop/uploadFile/"+num);
		if( file1.exists() ) file1.renameTo( fileNew1 );
	}

}
