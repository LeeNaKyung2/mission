package board_Servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/FileDownServlet")
public class FileDownServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
       
		String fileName = request.getParameter("number"); // �� function���� �������� 'name'������ number�� ������ ������
		String orgName = request.getParameter("file");
		String fileFolderPath = "C:/Users/user/Desktop/uploadFile";
		System.out.println("�ٿ�ε� ���� ���� ��� ��ġ : " + fileFolderPath);
		System.out.println("file1:" + fileName);
		String fullFilePath = fileFolderPath + "/" + fileName; // ����Ǿ� �ִ� �������/����� ���ϸ� ���� Ǯ path�� ������ش�

		try {
			File file = new File(fullFilePath);
			byte b[] = new byte[1024 * 1024 * 10];

			// ���� Ȯ�� : �о�� ����� ������ ���� -> ������ ������ �� Ÿ���� �����ؾ� �Ѵ�.
			String mimeType = getServletContext().getMimeType(file.toString());
			if (mimeType == null) {
				// ������ �˷����� ���� ���Ͽ� ���� �б� ���� ����
				response.setContentType("application/octet-stream");
			}

			String encoding = new String(orgName.getBytes("UTF-8"), "8859_1");
			// Content-Disposition: �������� �󿡼� �ٷ� ������ �ڵ����� �����ִ� ��
			// attachment;filename= : ��Ÿ ������ ���� ���������� �ٿ�ε� �� ȭ�鿡 ��½��� �ش�.
			response.setHeader("Content-Disposition", "attachment;filename=" + encoding + "");

			// ������ �о�;� ������ inputStream�� ����.
			FileInputStream fileInputStream = new FileInputStream(file);
			// �������� ����
			ServletOutputStream servletOutputStream = response.getOutputStream();

			int readNum = 0;
			while ((readNum = fileInputStream.read(b)) != -1) {
				servletOutputStream.write(b, 0, readNum);
			}
			servletOutputStream.flush(); // ���۸� ������ �о ���°�(flush ���ְ� close ������Ѵ�.)
			servletOutputStream.close();
			fileInputStream.close();

		} catch (Exception e) {
			
			
			System.out.println("Download\" Exception : " + e.getMessage());
		}
	}
}
