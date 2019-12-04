package board_Vo;

public class BoardInfoVo {
	
	int Num;
	String Title;
	String Contents;
	String File1;
	int Total; //전체 게시물 수
	
	public int getTotal() {
		return Total;
	}
	public void setTotal(int total) {
		Total = total;
	}
	public int getNum() {
		return Num;
	}
	public void setNum(int num) {
		this.Num = num;
	}
	public String getTitle() {
		return Title;
	}
	public void setTitle(String title) {
		Title = title;
	}
	public String getContents() {
		return Contents;
	}
	public void setContents(String contents) {
		Contents = contents;
	}
	public String getFile1() {
		return File1;
	}
	public void setFile1(String file1) {
		File1 = file1;
	}
}
