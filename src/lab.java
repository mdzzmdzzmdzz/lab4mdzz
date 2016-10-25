import java.awt.image.DataBufferDouble;
import java.beans.Expression;
import java.util.ArrayList;
import java.util.IllegalFormatCodePointException;
import java.util.Scanner;
import java.util.function.DoubleToLongFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.text.DateFormat;  
import java.text.ParseException;  
import java.text.SimpleDateFormat;  
import java.util.Calendar;  
import java.util.Date; 

import javax.imageio.stream.ImageInputStream;

import org.omg.CORBA.PUBLIC_MEMBER;

/*
 1.实现了小数的功能，但是小数一般不能作为幂运算的指数；
 2.实现了幂运算的功能（不仅局限于变量，系数的次方也可以计算成十进制）；
 3.数字于变量之间的*可以忽略不进行输入；
 4.可以包含空格、tab等字符； 
 5.多项式中的变量可以使用长度>1 的字符串表示； 
 6.在出现“+”的位置，可以使用 “-”； 
 测试用例：
//2*x*z+3*x*z^3-z^2+2
//!simplify x=2 
//!d/dz
 */
public class lab 
{
	public static ArrayList<Double> xishu =  new ArrayList<Double>() ;
	public static ArrayList<String> bianliang =new ArrayList<String>();
	public static ArrayList<String> jiajian =new ArrayList<String>();
	public static ArrayList<Double> xishu2 =new ArrayList<Double>();
	public static ArrayList<String> bianliang2 =new ArrayList<String>();
	public static ArrayList<String> bianliang3 =new ArrayList<String>();
	public static ArrayList<Double> xishu3 =new ArrayList<Double>();
	public static ArrayList<Double> xishunew =new ArrayList<Double>();
	public static ArrayList<String> bianliangnew =new ArrayList<String>();
	public static ArrayList<Integer> index=new ArrayList<Integer>();
	public static void main(String[] args)
	{	
		
		String s = null;
		Date d = new Date();
		
		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
        s = sdf.format(d);  
        System.out.println("当前时间为："+s); 
		
		String duoxiangshi="";
		String order1="";
		String order2="";
		while (!duoxiangshi.equals("exit"))
		{
			duoxiangshi="";
			duoxiangshi=In();//为了避免输入错误，规定输入的系数带有^必须放在所有系数的最后输入
			
			Calendar c = Calendar.getInstance();
			
			if (duoxiangshi.equals("exit"))
			{
				break;
			}
			duoxiangshi=Check(duoxiangshi);
			
			Calendar c1 = Calendar.getInstance();
			System.out.println("输出多项式算法共用时：" + (c1.getTimeInMillis()-c.getTimeInMillis())+"毫秒"); 
			
			xishu.clear();
			bianliang.clear();
			jiajian.clear();
			xishu2.clear();
			bianliang2.clear();
			bianliang3.clear();
			xishu3.clear();
			xishunew.clear();
			bianliangnew.clear();
			
			Expression1 (duoxiangshi);
			Expression2 (duoxiangshi);
			Expression3 (duoxiangshi);
			order1=In();
			Calendar c2 = Calendar.getInstance();
			Simplify(order1);
			Calendar c3 = Calendar.getInstance();
			System.out.println("赋值算法共用时：" + (c3.getTimeInMillis()-c2.getTimeInMillis())+"毫秒");
			
			xishu.clear();
			bianliang.clear();
			jiajian.clear();
			xishu2.clear();
			bianliang2.clear();
			bianliang3.clear();
			xishu3.clear();
			xishunew.clear();
			bianliangnew.clear();
			
			Expression1 (duoxiangshi);
			Expression2 (duoxiangshi);
			Expression3 (duoxiangshi);
			
			order2=In();
			Calendar c4 = Calendar.getInstance();
			Derivative(order2,duoxiangshi);
			Calendar c5 = Calendar.getInstance();
			System.out.println("求导算法共用时：" + (c5.getTimeInMillis()-c4.getTimeInMillis())+"毫秒");
			
		}	
		String s1 = null;
		Date d1 = new Date();
		DateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
        s1 = sdf1.format(d1);  
        System.out.println("当前时间为："+s1); 
	}
	public static String In()//输入函数
	{
		Scanner input=new Scanner(System.in);
		String duoxiangshi=input.nextLine();//读入多项式，将字符串保存到字符串duoxiangshi中
		return duoxiangshi;
	}
	public static String Check (String str)//检验输入是否含有非法字符
	{
		String regx0="[^	a-zA-Z0-9\\+\\-\\*\\^\\. ]";
		Pattern p = Pattern.compile(regx0);
		Matcher m = p.matcher(str);
		boolean flag=m.find();//查找非法输入的字符，输入的字符不能包括除了合法数字字母以及运算符之外的任何字符
		if(flag)
		{
			System.out.println("非法输入！请重新输入：");
		}//如果检验到非法字符则输入异常
		else
		{
			str=str.replaceAll(" *", "");//处理空格，将所有空格都替换成空
			str=str.replaceAll("	*", "");//处理tab，将所有空格都替换成空
			System.out.println(str);
			
		}
		return str;
	}
	public static void Expression1 (String str)//用于处理字符串中的系数，将多项式系数变成易于操作的数据结构

	{
		ArrayList<String> xishuf  =  new ArrayList<String>();
		String regx1="\\+|\\-";//按照+或者减号
		String[] arryinshi=str.split(regx1);//将整个多项式分解成多个因式
		for(String s:arryinshi)//对每个因式进行操作
		{
			xishuf.clear();
			boolean flag=true;
			//String regx2="([0-9]+\\.[0-9]+\\^?[1-9]*[0-9]*)|([1-9][0-9]*\\^?[1-9]*[0-9]*)";//找出系数
			String regx2="[1-9]+[0-9]*\\.?[0-9]*\\^?[1-9]*[0-9]*";//找出系数
			Pattern p=Pattern.compile(regx2);
			Matcher m=p.matcher(s);//取出每个因式中的数字项（即系数），另：如果系数中有次方比如2^3,则先不计算单独留出
			int index0=-1;
			while (m.find())
			{
				//index0++;
				index0=s.indexOf(m.group(),index0+1);
				if (index0>=1)
				{
					if(s.charAt(index0-1)!='^' & s.charAt(index0-1)!='.')
					{
						xishuf.add(m.group());
					}
				}
				else
				{
					xishuf.add(m.group());
				}
			}
			for(String s1:xishuf)//找出乘方形式的系数,并计算结果
			{
				String regx3="([0-9]+\\.[0-9]+\\^[1-9]*[0-9]*)|([1-9][0-9]*\\^[1-9]*[0-9]*)";
				Pattern pattern = Pattern.compile(regx3);
				Matcher matcher = pattern.matcher(s1);
				boolean b= matcher.matches();
				if(b)
				{
					flag=false;
				}
				if(!flag)//此时s1是a^b的形式
				{
					double t1=0;
					double t2=0;
					int index1;
					String regx4="\\^";
					String[] arrtemp=s1.split(regx4);
					t1=Double.parseDouble(arrtemp[0]);//将底数和指数分别存入t1和t2
					t2=Double.parseDouble(arrtemp[1]);
					t1=Math.pow(t1,t2);//计算乘方的值
					index1=xishuf.indexOf(s1);
					xishuf.set(index1, String.valueOf(t1));//用计算结果替换掉原来的a^b形式
				}
			}
			double num=1.0;
			for(String s2:xishuf)
			{
				num=num*Double.parseDouble(s2);
			}
			xishu.add(num);
		}
		//System.out.print(xishu);
		xishunew=xishu;
		//System.out.print('\n');
	}
	public static void Expression2 (String str)//用于处理字符串中的变量，将多项式系数变成易于操作的数据结构

	{
		String regx1="\\+|\\-";//按照+或者减号
		String[] arryinshi=str.split(regx1);//将整个多项式分解成多个因式
		bianliang.clear();
		ArrayList<String>chengfang=new ArrayList<String>();
		ArrayList<String>bianliangf=new ArrayList<String>();
		String regx2="\\*?[a-z]+\\^[1-9]+[0-9]*";//用于寻找幂运算
		String regx3="\\*?[a-z]+";//用于寻找x 或者 *x
		for(String s:arryinshi)
		{
			chengfang.clear();
			bianliangf.clear();
			Pattern p=Pattern.compile(regx2);
			Matcher m=p.matcher(s);
			while (m.find())
			{
				chengfang.add(m.group());
			}
			s=s.replaceAll(regx2, "");
			Pattern p1=Pattern.compile(regx3);
			Matcher m1=p1.matcher(s);
			while (m1.find())
			{
				bianliangf.add(m1.group());
			}
			String temp="";
			for(String s1:bianliangf)
			{
				temp=temp+s1;
			}
			for(String s2:chengfang)
			{
				temp=temp+s2;
			}
			bianliang.add(temp);
		}
		bianliangnew=bianliang;
		//System.out.println(bianliang);
		
	}
	public static void Expression3(String str)//用于处理字符串中的加减号

	{
		String regx="[+-]";
		Pattern p=Pattern.compile(regx);
		Matcher m=p.matcher(str);
		while (m.find())
		{
			jiajian.add(m.group());
		}
		//System.out.println(jiajian);
	}
	public static void Expression4(String str)

	{
		ArrayList<String >xishuf=new ArrayList<String >();
		boolean flag=true;
		//String regx2="([0-9]+\\.[0-9]+\\^?[1-9]*[0-9]*)|([1-9][0-9]*\\^?[1-9]*[0-9]*)";//找出系数
		String regx2="[1-9]+[0-9]*\\.?[0-9]*\\^?[1-9]*[0-9]*";//找出系数
		Pattern p=Pattern.compile(regx2);
		Matcher m=p.matcher(str);//取出每个因式中的数字项（即系数），另：如果系数中有次方比如2^3,则先不计算单独留出
		int index0=-1;
		while (m.find())
		{
			//index0++;
			index0=str.indexOf(m.group(),index0+1);
			if (index0>=1)
			{
				if(str.charAt(index0-1)!='^' & str.charAt(index0-1)!='.')
				{
					xishuf.add(m.group());
				}
			}
			else
			{
				xishuf.add(m.group());
			}
		}
		for(String s1:xishuf)//找出乘方形式的系数,并计算结果
		{
			String regx3="([0-9]+\\.[0-9]+\\^[1-9]*[0-9]*)|([1-9][0-9]*\\^[1-9]*[0-9]*)";
			Pattern pattern = Pattern.compile(regx3);
			Matcher matcher = pattern.matcher(s1);
			boolean b= matcher.matches();
			if(b)
			{
				flag=false;
			}
			if(!flag)//此时s1是a^b的形式
			{
				double t1=0;
				double t2=0;
				int index1;
				String regx4="\\^";
				String[] arrtemp=s1.split(regx4);
				t1=Double.parseDouble(arrtemp[0]);//将底数和指数分别存入t1和t2
				t2=Double.parseDouble(arrtemp[1]);
				t1=Math.pow(t1,t2);//计算乘方的值
				index1=xishuf.indexOf(s1);
				xishuf.set(index1, String.valueOf(t1));//用计算结果替换掉原来的a^b形式
			}
		}
		double num=1.0;
		for(String s2:xishuf)
		{
			num=num*Double.parseDouble(s2);
		}
		xishu2.add(num);
	}
	public static void Expression5(String str)

	{
		ArrayList<String>chengfang=new ArrayList<String>();
		ArrayList<String>bianliangf=new ArrayList<String>();
		String regx2="\\*?[a-z]+\\^[1-9]+[0-9]*";//用于寻找幂运算
		String regx3="\\*?[a-z]+";//用于寻找x 或者 *x
		
			chengfang.clear();
			bianliangf.clear();
			Pattern p=Pattern.compile(regx2);
			Matcher m=p.matcher(str);
			while (m.find())
			{
				chengfang.add(m.group());
			}
			str=str.replaceAll(regx2, "");
			Pattern p1=Pattern.compile(regx3);
			Matcher m1=p1.matcher(str);
			while (m1.find())
			{
				bianliangf.add(m1.group());
			}
			String temp="";
			for(String s1:bianliangf)
			{
				temp=temp+s1;
			}
			for(String s2:chengfang)
			{
				temp=temp+s2;
			}
			bianliang2.add(temp);
		
	}
	public static void Output()
	{
		jiajian.add("");
		String temp2="";
		for (int j=0 ;j< bianliang2.size() & j< xishu2.size();j++ )
		{
			String temp1="";
			temp1=xishu2.get(j)+bianliang2.get(j)+jiajian.get(j);
			temp2=temp2+temp1;
		}
		System.out.print(temp2);
		System.out.print("\n");
	}
	public static void Output2()
	{
		String temp2="";
		String temp4="";
		String temp5="";
		jiajian.add("");
		for (int j=0 ;j< bianliang3.size() & j< xishu3.size();j++ )
		{
			
				int falg=1;
				for(int i=0;i<index.size();i++)
				{
					if(index.get(i)==j)
					{
						falg=0;
					}
				}
				if(falg==1)
				{
					String temp1="";
					temp1=xishu3.get(j)+bianliang3.get(j)+jiajian.get(j);
					temp2=temp2+temp1;
				}			
		}
		Pattern p=Pattern.compile("[0-9]+\\.[0-9]+[a-z]+");
		Matcher m=p.matcher(temp2);
		while (m.find())
		{
			String temp3=m.group();
			Pattern p1=Pattern.compile("[0-9]+\\.[0-9]*");
			Matcher m1=p1.matcher(temp3);
			while (m1.find())
			{
				temp4=m1.group();
			}
			temp5= temp4 + "*";
		}
		temp2=temp2.replaceAll(temp4, temp5);
		temp2=temp2.replaceAll("\\*\\+", "\\+");
		temp2=temp2.replaceAll("\\*\\-", "-");
		temp2=temp2.replaceAll("\\*$", "");
		temp2=temp2.replaceAll("\\*\\*", "\\*");
		temp2=temp2.replaceAll("\\+$|\\-$", "");
		System.out.print(temp2);
		System.out.print("\n");
	}
	public static void Simplify(String order)


	{
		ArrayList<String >bianliangx=new ArrayList<String>();
		bianliangx=bianliangnew;
		ArrayList<String >fuzhi=new ArrayList<String>();
		ArrayList<String >result=new ArrayList<String >();
		order=order.replaceAll("!simplify", "");
		order=order.replaceAll(" +", "");
		order=order.replaceAll("	+", "");
		String regx="[a-z]+=[0-9]+";
		Pattern p=Pattern.compile(regx);
		Matcher m=p.matcher(order);//取出每个因式中的数字项（即系数），另：如果系数中有次方比如2^3,则先不计算单独留出
		while (m.find())
		{
			fuzhi.add(m.group());
		}
		for (String s1 :fuzhi)
		{
			String regx2="=";
			String[] arrtemp=s1.split(regx2);
			String s2;
			for(int i=0;i<bianliangx.size();i++)
			{
				s2=bianliangx.get(i);
				s2=s2.replaceAll(arrtemp[0], arrtemp[1]);
				bianliangx.remove(i);
				bianliangx.add(i, s2);
			}
		}
		for (int j=0 ;j< bianliangx.size() & j< xishu.size();j++ )
		{
			String temp1="";
			temp1=xishu.get(j)+"*"+bianliangx.get(j);
			String regx7="\\*{2}";
			temp1=temp1.replaceAll(regx7, "\\*");
			result.add(temp1);
		}
		for(String s2: result)
		{
			Expression4(s2);
		}
		for(String s3: result)
		{
			Expression5(s3);
		}
		Output();
	}
	public static void Derivative(String order,String duoxiangshi)
	{
		order=order.replaceAll("!d/d", "");
		order=order.replaceAll(" +", "");
		order=order.replaceAll("	+", "");
		for(int m=0;m<bianliangnew.size();m++)
		{
			String temp=bianliangnew.get(m);
			bianliang3.add(temp);
		}
		xishu3=xishunew;
		for(int i=0;i<bianliang3.size();i++)
		{
			Pattern p=Pattern.compile(order);
			Matcher m=p.matcher(bianliang3.get(i));
			String temp="";
			String regx1="\\*?"+order+"\\^[0-9]+";
			
			while (m.find())
			{
				String temp3=m.group();
				int index0=bianliang3.get(i).indexOf(temp3);
				if( index0+1<bianliang3.get(i).length())
				{	
					if( bianliang3.get(i).charAt(index0+1)=='^')
					{
						Pattern p1=Pattern.compile(regx1);
						Matcher m1=p1.matcher(bianliang3.get(i));
						while (m1.find())
						{
							temp=m1.group();
						}
						String[] temp1 = temp.split("\\^");
						int temp2=Integer.parseInt(temp1[1]);
						if (temp2==2)
						{
							double num=xishu3.get(i);
							num=num*2.0;
							xishu3.remove(i);
							xishu3.add(i,num);
							String s1=bianliang3.get(i);
							s1=s1.replaceAll(order+"\\^2", order);
							bianliang3.remove(i);
							bianliang3.add(i,s1);
						}
						else 
						{
							double num=xishu3.get(i);
							num=num*(temp2);
							xishu3.remove(i);
							xishu3.add(i,num);
							String s2=bianliang3.get(i);
							s2=s2.replaceAll(order+"\\^"+temp1[1], order+"\\^"+String.valueOf(temp2-1));
							bianliang3.remove(i);
							bianliang3.add(i,s2);
						}
					}
										
				}
				else
				{
					String s2=bianliang3.get(i);
					s2=s2.replaceAll("\\*?"+order, "");
					bianliang3.remove(i);
					bianliang3.add(i, s2);
				}
			}
						
		}
		for(int k=0;k<bianliang3.size();k++)
		{
			Pattern p3=Pattern.compile(order);
			Matcher m3=p3.matcher(bianliangnew.get(k));
			if (!m3.find())
			{
				index.add(k);
			}
		}
		Output2();
	}
}
