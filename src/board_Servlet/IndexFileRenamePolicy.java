package board_Servlet;

import java.io.File;
import com.oreilly.servlet.multipart.FileRenamePolicy;

	public class IndexFileRenamePolicy implements FileRenamePolicy {   //A��C��� �������̽��� �����ϰڴ�
		int index;
		public  IndexFileRenamePolicy(int num){
			this.index=num;
	}
	
	@Override
	public File rename(File f) {
		
       String name = f.getName();

       String newName = index+"";
       f = new File(f.getParent(), newName);
       

       return f;
	}

}
