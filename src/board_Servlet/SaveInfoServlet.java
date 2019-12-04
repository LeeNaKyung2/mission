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
		
		
		String uploadFilePath = "C:/Users/user/Desktop/uploadfile";  //���� ��μ���
		File fileSaveDir = new File(uploadFilePath);
		System.out.println("������ : " + uploadFilePath);
		int maxSize =1024 *1024 *10;
 	    
 	   	MultipartRequest multi =null;//÷�������� �ޱ����� �޼ҵ�
		Connection conn = null;
		
		try {
			Context initCtx = new InitialContext();   //InitialContext ������ �����
			Context envCtx = (Context) initCtx.lookup("java:comp/env");   // lookup =�޼ҵ��̸�-������ ã�´�. / "java:comp/env"�� �ڹ� ��������� ã�� �� �ִ� JNDIƮ���� ���
			DataSource ds = (DataSource) envCtx.lookup("jdbc/Oracle11g");
			conn = ds.getConnection();
			//�����Ҽ� �ִ� ������ ���ٸ� ����� ��
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
	    
	    ResultSet rs = null;  //select�� ����ÿ� ����� ����
		PreparedStatement pstmtIndex = null;  //SQL���� ����� �� setter�� �̿��� ���� ����(select ��)
		
		try {
			String index = "select num.NEXTVAL as line from dual"; // as�� num.NEXTVAL�� ���� �̸��� ������� dual�� ��ȸ�� ���̺�
			pstmtIndex = conn.prepareStatement(index);
			rs = pstmtIndex.executeQuery(); // select �������� ���� ¦ó�� �ٳ����
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
 	   	String textFileName="";  //���� ���� ��
 	    
		PreparedStatement pstmtInsertContents = null;  //SQL���� ����� �� setter���� �̿��� ���� ����(insert ��)
		
		try {
			pstmtInsertContents = conn.prepareStatement("insert into Screen(num,title,contents,file1) values(?,?,?,?)");
			title = multi.getParameter("title");
			contents = multi.getParameter("contents");

			Enumeration<?> stepFiles = multi.getFileNames(); // ������ ��ü���� �Ѽ����� �ϳ��� ó�� �Ҽ��մ� �޼ҵ带 ���� = ������ ��ü �����̸����� ������
			String realFile = (String) stepFiles.nextElement();
			textFileName = multi.getOriginalFileName(realFile);// ���� ���ϸ��� ��ȯ ���ϸ� ������
			// insert�� ?���� �Է�
			pstmtInsertContents.setInt(1, line);
			pstmtInsertContents.setString(2, title);
			pstmtInsertContents.setString(3, contents);
			pstmtInsertContents.setString(4, textFileName);
			pstmtInsertContents.executeUpdate(); // insert �������� ���� ¦ó�� �ٳ����

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

