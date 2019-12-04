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
       
		String fileName = request.getParameter("number"); // 상세 function에서 설정해준 'name'값으로 number로 파일을 가져옴
		String orgName = request.getParameter("file");
		String fileFolderPath = "C:/Users/user/Desktop/uploadFile";
		System.out.println("다운로드 폴더 절대 경로 위치 : " + fileFolderPath);
		System.out.println("file1:" + fileName);
		String fullFilePath = fileFolderPath + "/" + fileName; // 저장되어 있는 폴더경로/저장된 파일명 으로 풀 path를 만들어준다

		try {
			File file = new File(fullFilePath);
			byte b[] = new byte[1024 * 1024 * 10];

			// 유형 확인 : 읽어올 경로의 파일의 유형 -> 페이지 생성할 때 타입을 설정해야 한다.
			String mimeType = getServletContext().getMimeType(file.toString());
			if (mimeType == null) {
				// 유형이 알려지지 않은 파일에 대한 읽기 형식 지정
				response.setContentType("application/octet-stream");
			}

			String encoding = new String(orgName.getBytes("UTF-8"), "8859_1");
			// Content-Disposition: 웹브라우저 상에서 바로 파일을 자동으로 보여주는 거
			// attachment;filename= : 기타 내용을 보고 브라우저에서 다운로드 시 화면에 출력시켜 준다.
			response.setHeader("Content-Disposition", "attachment;filename=" + encoding + "");

			// 파일을 읽어와야 함으로 inputStream을 연다.
			FileInputStream fileInputStream = new FileInputStream(file);
			// 브라우저에 쓰기
			ServletOutputStream servletOutputStream = response.getOutputStream();

			int readNum = 0;
			while ((readNum = fileInputStream.read(b)) != -1) {
				servletOutputStream.write(b, 0, readNum);
			}
			servletOutputStream.flush(); // 버퍼를 강제로 밀어서 비우는거(flush 해주고 close 해줘야한다.)
			servletOutputStream.close();
			fileInputStream.close();

		} catch (Exception e) {
			
			
			System.out.println("Download\" Exception : " + e.getMessage());
		}
	}
}
