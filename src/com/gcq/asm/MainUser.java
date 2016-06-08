package com.gcq.asm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;


public class MainUser {

	public static void generateByAsm() {
		System.out.println();
		ClassWriter classWriter = new ClassWriter(0);
		// 通过visit方法确定类的头部信息
		classWriter.visit(Opcodes.V1_7,// java版本
		Opcodes.ACC_PUBLIC,// 类修饰符
		"com/gcq/asm/People", // 类的全限定名
		null, "java/lang/Object", null);
		//创建构造函数
		MethodVisitor mv = (MethodVisitor) classWriter.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
		mv.visitCode();
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>","()V");
		mv.visitInsn(Opcodes.RETURN);
		mv.visitMaxs(1, 1);
		mv.visitEnd();
		// 定义sayHello方法
		MethodVisitor methodVisitor = (MethodVisitor) classWriter.visitMethod(Opcodes.ACC_PUBLIC, "sayHello", "()V",
		null, null);
		methodVisitor.visitCode();
		methodVisitor.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out",
		"Ljava/io/PrintStream;");
		methodVisitor.visitLdcInsn("hello");
		methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println",
		"(Ljava/lang/String;)V");
		methodVisitor.visitInsn(Opcodes.RETURN);
		methodVisitor.visitMaxs(2, 2);
		methodVisitor.visitEnd();
		classWriter.visitEnd();
		// 使classWriter类已经完成
		// 将classWriter转换成字节数组写到文件里面去
		byte[] data = classWriter.toByteArray();
		File file = new File("./bin/com/gcq/asm/People.class");
		if(file.exists()) {
			file.delete();
		}
		FileOutputStream fout;
		try {
			file.createNewFile();
			fout = new FileOutputStream(file);
			fout.write(data);
			fout.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void generateByJavassist() {
		ClassPool pool = ClassPool.getDefault();
		//创建People类   
		CtClass cc= pool.makeClass("com.gcq.asm.People");
		try {
			//定义sayHello方法
			CtMethod method = CtNewMethod.make("public void sayHello(){}", cc);
			//插入方法代码
			method.insertBefore("System.out.println(\"hello\");");
			cc.addMethod(method);
			File file = new File("./bin/com/gcq/asm/People.class");
			if(file.exists()) {
				file.delete();
			}
			file.createNewFile();
			//保存生成的字节码
			cc.writeFile("./bin/");
		} catch (CannotCompileException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void load() {
		File file = new File(".");
		int count;
		InputStream input;
		byte[] result;
		try {
			input = new FileInputStream(file.getCanonicalPath()+"/bin/com/gcq/asm/People.class");
			result = new byte[1024];
			count = input.read(result);
			// 使用自定义的类加载器将 byte字节码数组转换为对应的class对象
			MyClassLoader loader = new MyClassLoader();
			Class clazz = loader.defineMyClass( result, 0, count);
			//测试加载是否成功，打印class 对象的名称
			System.out.println(clazz.getCanonicalName());
			//实例化一个People对象
			Object o = clazz.newInstance();
			//调用People的sayHello方法
			clazz.getMethod("sayHello", null).invoke(o, null);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
//		generateByAsm();
		generateByJavassist();
		load();
	}

}
