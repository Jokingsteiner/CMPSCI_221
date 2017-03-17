import java.util.*;
import java.io.*;

public class NDCG {
	private static final ArrayList<Double> Googlerank=new ArrayList(){{add(5.0);add(4.0);add(3.0);add(2.0);add(1.0);}};
	public NDCG(){
		
	}
	    
	public double calNDCG(ArrayList<String> mytop5,int number){
		String filename=".\\Googletop5.txt";
		FileReaderWBuffer fr = new FileReaderWBuffer(filename);
        String lineTxt=null;
        HashMap<String,Integer> Google=new HashMap<>();
        
        //ArrayList<String> Google=new ArrayList();
        ArrayList<urlscore> myresult=new ArrayList<>();
        int n=1+6*number;//read from line n
        while (n -- > 1 ) {
            fr.readLine();
        }
        int i=5;
        while((lineTxt = fr.readLine()) != null){
           if(!lineTxt.isEmpty()){
        	   Google.put(lineTxt,i);
        	   i--;
        	   if(i==0) {
        		   i=5;break;
        		   }
        	   }
        }
        fr.close();

        //System.out.println(Google.values());
        //System.out.println(Google.keySet());
        
       /*get mytop5 url score*/
        i=0;
        for(i=5*number;i<5+5*number;i++){
        	if( Google.containsKey(mytop5.get(i)) ) {
        		myresult.add(new urlscore(mytop5.get(i),Google.get(mytop5.get(i))));
        	}
        	else myresult.add(new urlscore(mytop5.get(i),0));
        }
     
/*        for(i=0;i<5;i++){
           System.out.print(myresult.get(i).getscore());
        }
        System.out.println();*/
       ArrayList<Double> mydiscountedgain=new ArrayList<>();
       ArrayList<Double> Googlediscountedgain=new ArrayList<>();
       double temp=0;
       for(i=0;i<5;i++){
    	   if(i>0){
    		   temp=myresult.get(i).getscore()/(Math.log(i+1)/Math.log(2));
    	   }else{
    		   temp=myresult.get(i).getscore();
    	   }
    	   mydiscountedgain.add(temp);
       }
       
       temp=0;
       for(i=0;i<5;i++){
    	   if(i>0){
    		   temp=Googlerank.get(i)/(Math.log(i+1)/Math.log(2));
    	   }
    	   else temp=Googlerank.get(i);
    	   Googlediscountedgain.add(temp);
       }
  
       ArrayList<Double> myDCG=calDCG(mydiscountedgain);
       ArrayList<Double> GoogleDCG=calDCG(Googlediscountedgain);
       ArrayList<Double> myNDCG=calNDCG(myDCG,GoogleDCG);
       
//       FileWriterWBuffer fw = new FileWriterWBuffer(".\\testResult.txt", true);
//       String writeLine=String.valueOf(myNDCG.get(0));
//       for (i=1;i<5;i++) {
//           writeLine = writeLine+' '+ String.valueOf(myNDCG.get(i));
//           
//       }
//       writeLine=writeLine+'\n';
//       fw.writeLine(writeLine);
//       fw.close();
       
//       for(double e : myNDCG){
//    	   System.out.println(String.valueOf(e));
//       }
       return myNDCG.get(4);
	}
	
	private ArrayList<Double> calDCG(ArrayList<Double> score){
		ArrayList<Double> result=new ArrayList<>();
		double temp=0;
	       for(int i=0;i<5;i++){
	    	   temp=temp+score.get(i);
	    	   result.add(temp);
	       }
	   return result;
	}
	private ArrayList<Double> calNDCG(ArrayList<Double> score1,ArrayList<Double> score2){
		ArrayList<Double> result=new ArrayList<>();
		double temp=0;
	       for(int i=0;i<5;i++){
	    	   temp=score1.get(i)/score2.get(i);
	    	   result.add(temp);
	       }
		 return result;
	}
	

	
	public static void main (String arg[]){
		NDCG cal=new NDCG();
		ArrayList<String> mytop5=new ArrayList<>();
		String filename=".\\mytop5.txt";
		FileReaderWBuffer fr = new FileReaderWBuffer(filename);
        String lineTxt=null;
        while((lineTxt = fr.readLine()) != null){
        	 if(!lineTxt.isEmpty()){
        		 mytop5.add(lineTxt);
        	 }
          
        }
        fr.close();
//		mytop5.add("mondego.ics.uci.edu");	
//		mytop5.add("mondego.ics.uci.edu/projects/SourcererCC");
//		mytop5.add("mondego.ics.uci.edu/icsergen");
//		mytop5.add("www.ics.uci.edu/~lopes");
//		mytop5.add("mondego.ics.uci.edu/projects/clonedetection");
        //System.out.println(mytop5.size());
        for(int i=0;i<10;i++){
			cal.calNDCG(mytop5,i);
	    }

		System.out.println("\nsuccess");
	}
}
