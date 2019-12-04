	$(document).ready(function(){ //처음 화면이 띄워지자마자 나타나는 화면
		
		//리스트 페이징 , 검색기능
		paging('1');
		
		//검색에서 엔터키 눌렀을때 이벤트 실행
		$("#search").keydown(function(key){
			if(key.keyCode == 13){
				paging('1');
			}
		});
		
		//새글이 등록됨
		$("#sumbitBtn").click(function(){
			enrollPage();
		});
		//검색기록 출럭
		$("#searchBtn").click(function(){
			paging('1');
		});
		
		//데이터가 수정됨
		$("#mod").click(function(){
			modifyPage();
		});
		
		//데이터가 삭제됨
		$("#del").click(function(){
    		deletePage();
    	});
	});	
		
	
	//게시물 리스트 페이징처리 , 검색기능
	function paging(curPage) {
		
		var searchType=$('#searchType option:selected').val();
		var search=$('#search').val();
		
		$.ajax({
			type : "GET",
			url : "/board/BoardPagingandSearch",
			datatype : 'json',
			data:{
				curPage:curPage,
				searchType:searchType,
				searchContents:search
			},
			success : function(data) {
				var list = JSON.parse(data);
				var totalCount = list[0].Total;
				var atr = '';
				var startBlock=Math.floor((curPage-1)/10)*10+1; //시작 페이지
				var endBlock=startBlock+9; //끝 페이지
				
				var str = "<table class=\"table table-hover table table-bordered\">";
				str +="<tr>";
				str +="	<td class=\"text-center success col-md-1\">Index</td>";
				str +="	<td class=\"text-center success\">제목</td>";
				str +="	<td class=\"text-center success\">내용</td>";
				str +="	<td class=\"text-center success\">첨부파일</td>";
				str +="</tr>";
				
				for(var i=0; i < list.length; i++){	
					var num = list[i].Num;
					var title = list[i].Title;
					var contents = list[i].Contents;
					var saveFile = list[i].File1;
					
					//onclick 은 상세보기를 위한 설정
					str +="<tr onclick='detailPage("+num+");'><td>"+num+"</td>"; 
					str +="<td>"+title+"</td>";
					str +="<td>"+contents+"</td>";
					str +="<td>"+saveFile+"</td></tr>";
				}
				
				if(endBlock>totalCount){
					endBlock=totalCount;
				}
				if(startBlock<=10){
					atr += "<li class='page-item disabled'><a class='page-link' href='javascript:paging ("+ (startBlock-1) +")' > 이전 </a></li>"; 
				} //disabled 는 클릭안되게 처리하는거
				if(startBlock >= 11){
					atr += "<li class='page-item'><a class='page-link' href='javascript:paging ("+ (startBlock-1) +") '> 이전 </a></li>";
				}
				
				for (var j =startBlock; j <= endBlock; j++) {
					if(j==curPage){
						atr += '<li class="page-item"><a href="javascript:paging (' + j + ')" class="page-link">' +" "+ j + '</a></li>';
					}else{	
						atr += '<li class="page-item"><a href="javascript:paging (' + j + ')" class="page-link">' +" "+ j + '</a></li>';
					}
				}
				if(endBlock<totalCount){
					atr += "<li class='page-item'><a href='javascript:paging("+ (endBlock + 1) +")' class='page-link' > 다음 </a></li>";
				}
				if(endBlock>=totalCount){
					atr += "<li class='page-item disabled'><a href='javascript:paging("+ (endBlock + 1) +")' class='page-link' > 다음 </a></li>";
				}
				
				
				str +="</table>";
				$('#boardList').html(str);
				$('#pageTest').html(atr);
				
			},
			error:function(request,status,error){
				alert("code:"+request.status+"\n"+"message:"+request.responseText+"\n"+"error:"+error);
			}
		});
		//등록버튼 누르면  modal창이 나옴
		$("#enrollBtn").click(function(){
			$("#enrollModal").modal();
		});
	}
	
	//새 글 등록하기
	function enrollPage(){
		
		 var form =$('#enroll')[0]; //#enroll은 폼안에 준 id값
		 var data = new FormData(form);
		 
		 $.ajax({
	        type: "POST",
	        url:"/board/SaveInfoServlet",
	        enctype : 'multipart/form-data',   //첨부파일을 보내기위한 type
	        processData: false,  //formData를 string으로 변환하지 않음
            contentType: false,  //content-type 헤더가 multipart/form-data로 전송되게 함
			datatype : 'json',
			data : data,
			success : function(data) {
				 alert("저장 되었습니다.");
				 $("#enrollModal").modal('hide'); //모달창 닫아주기
				 paging('1');
			},
			error : function(data) {
				alert("에러발생");
			}
		})
	}
	
	//목록누르면 뜨는 상세페이지
	function detailPage(num){
		//index 값으로 data를 보냄
		$("#detailModal").modal();
		
 			$.ajax({
 				type : "GET",
 				url : "/board/BoardDetailServlet",
 				data :{
 					num:num
 				},
 				datatype : 'json',
 				success : function(data) {
 					var obj = JSON.parse(data);
 					var str = '<table class="table table table-bordered">';
 					str +="<tr>";
 					str +="<h4>";
 					str +='<td class="text-center success col-md-1">index</td>';
 					str +='<td id="num"><input type="hidden" name="number" value='+obj[0].Num+'>'+obj[0].Num+'</td>';    //num 값이 필요하기 때문에 td에 id값을 줌
 					str +="</h4>";
 					str +="</tr>";
 					str +="<tr>";
 					str +="<h4>";
 					str +='<td class="text-center success col-md-1">제목</td>';
 					str +='<td><input id="detail_title" type="text" name="detail_title1" value='+obj[0].Title+'></td>';
 					str +="</h4>";
 					str +="</tr>";
 					str +="<tr>";
 					str +="<h4>";
 					str +='<td class="text-center success col-md-1">내용</td>';
 					str +='<td><input id="detail_contents" type="text" name="detail_contents1" value='+obj[0].Contents+'></td>';
 					str +="</h4>";
 					str +="</tr>";
 					str +="<tr>";
 					str +="<h4>";
 	 				str +='<td class="text-center success col-md-1">첨부파일</td>';
 	 				str +='<td><a href="javascript:document.FileDown.submit();">'+obj[0].File1 +'<input type="file" name="modFile" id="modFile" value=""></td>'; //"javascript:"는 리턴값이 출력되므로 리턴 값이 있으면 안됨>
 					str +="</h4>";
 					str +="</tr>";
 					str +="</table>";
 					$('#detailList').html(str);
 					
 					str ='<input type="hidden" name="title" value="'+obj[0].Title+'"/>';
 				    str +='<input type="hidden" name="contents" value="'+obj[0].Contents+'"/>'; 
 					str +='<input type="hidden"  name="number" value="'+obj[0].Num +'"/>';
 					str +='<input type="hidden"  name="file" value="'+obj[0].File1 +'"/>';
 					$('#fileDownloadCall').html(str);
 				}
 			});
	}
	
	//테이블이 수정됨
	function modifyPage(){  
		//.val() = 폼을 가져오거나 값을 설정
		//id를 준 title과 contents의 값을 value~에 저장
		var num= $("#num").text();  //td에 id값을 주면 .text()로 가지고 옴
		var valueBytitle = $("#detail_title").val();
		var valueBycontents = $("#detail_contents").val();
		var valueByfile = $("#modFile").val();
		
		var form =$('#detailPage')[0]; //#enroll은 폼안에 준 id값
		var data = new FormData(form);
        
		$.ajax({
			type : "POST",
			url : "/board/BoardModifyServlet",
			enctype : 'multipart/form-data',   //첨부파일을 보내기위한 type
	        processData: false,  //formData를 string으로 변환하지 않음
	        contentType: false,  //content-type 헤더가 multipart/form-data로 전송되게 함
	        data : data,
            datatype : 'json',
			success : function(data) {
				alert("수정됩니다.");
				$("#detailModal").modal('hide');
				paging('1');
				
			},
			error : function(data) {
				alert("에러발생");
			}
		});
	}
	
	//테이블이 삭제됨
	function deletePage(){
		var num= $("#num").text();
		
		$.ajax({
			type : "GET",
			url : "/board/BoardDeleteServlet",
			data :{
				num:num
			},
			datatype : 'json',
			success : function(data) {
				alert("글이 삭제됩니다.");
				$("#detailModal").modal('hide');
				paging('1');
			},
			error : function(data) {
				alert("에러발생");
			}
		});
	}

	


 				