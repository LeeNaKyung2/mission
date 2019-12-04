<%@page import="java.sql.Statement"%>
<%@page import="board_Servlet.IndexFileRenamePolicy"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="java.sql.DriverManager"%>
<%@page import="java.sql.PreparedStatement"%>
<%@page import="java.sql.Connection"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<meta charset="UTF-8">
<head>
	<title>게시판 목록</title>
	<script src="/board/lib/jQuery/jquery-3.4.1.min.js"></script>
	<script src="/board/lib/jQuery/jquery-3.4.1.js"></script>
	<script src="/board/lib/BootStrap/js/bootstrap.min.js"></script>
<!-- js 파일 보내주고 import하기  -->
 	<script src="/board/js/Board.js"></script>
	<link rel="stylesheet" href="/board/lib/BootStrap/css/bootstrap.css">
	<link rel="stylesheet" href="/board/lib/BootStrap/css/bootstrap-theme.css">
</head>
<body>
	<h1 class ="text-center">
			<label for="exampleInputFile">게시판 목록</label>
	</h1>
	
	<h3>
		<label for="exampleInputFile">● 목록</label>
	</h3>
	
	<!-- 새 글 등록 모달 -->
<form  method="Post" id="enroll"> 
   <div class="modal fade" id="enrollModal" role="dialog"> 
	<div class="modal-dialog">
		
     <div class="modal-content">
     	 <div class="modal-header">
          <button type="button" class="close" data-dismiss="modal">×</button>
          <h4 class="modal-title">등록화면</h4>
         </div>
         <div class="modal-body">
        	<h4>
				제목:<input type=text name=title id="title" class="form-control" > 
				내용:<input type=text name=contents id="contents" class="form-control input-lg"> 
			</h4>
				파일:<input type=file name=saveFile id="saveFile">
        </div>
        <div class="modal-footer">
        	<button type="button" value="upload" class="btn btn-info btn-sm" id="sumbitBtn" >저장</button>
          	<button type="button" class="btn btn-default btn-sm" data-dismiss="modal">닫기</button>
        </div>
      </div>
    </div>
   </div>
 </form>
	<div align="right">
			<button type="button" class="btn btn-info btn-sm" id="enrollBtn">등록</button>
	</div>
	<div align="right">
        <!--select box는 원하는 값을 선택할수있는 입력 폼 -->
		<select name="searchType" id="searchType"> 
			<option value = "title"> 제목 </option>
			<option value = "contents"> 내용 </option>
		</select>
		<input type = "text" name="search" id="search" value=""/>
		<button type="button" name="searchbt" id="searchBtn" class="btn btn-success btn-sm">검색</button>
	</div>
	
	<div>
		<div id='boardList'></div>
        <!--페이징 부트스트랩 -->
		<nav aria-label="Page navigation example" style="text-align: center;">
			<ul class="pagination pagination-sm" id='pageTest' ></ul>
		</nav>
   </div>

	<!-- 상세보기,수정,삭제 모달 -->
	
	<div class="modal fade" id="detailModal" role="dialog">
		<div class="modal-dialog">
		 <div class="modal-content">
     	  <div class="modal-header">
          <button type="button" class="close" data-dismiss="modal">×</button>
          <h4 class="modal-title">내용보기</h4>
       	  </div>
        <div class="modal-body">
        	<form  method="Post" id="detailPage" enctype="multipart/form-data"> 
        		<div id='detailList'></div>
        	</form>
        	<form name="FileDown" action="/test03/FileDownServlet" method="GET">
        		<div id='fileDownloadCall'></div>
        	</form>
        </div>
       <div class="modal-footer">
        <div align="center">
			<button type="button" id="mod" class="btn btn-warning btn-sm" >수정</button>
			<button type="button" id="del" class="btn btn-danger btn-sm"  >삭제</button>
		</div>
       </div>
      </div>
    </div>
  </div>

</body>
</html>
	
	