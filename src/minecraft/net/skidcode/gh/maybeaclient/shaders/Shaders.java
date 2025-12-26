package net.skidcode.gh.maybeaclient.shaders;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.lwjgl.opengl.ARBFragmentShader;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.ARBVertexShader;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.skidcode.gh.maybeaclient.Client;

public class Shaders {
	//public static int shaderprogs[] = new int[1];
	//public static final int S_NONE = 0;
	//public static int S_TEST = 0;
	
	public static void init() {
	//	System.out.println("Initializing shaders");
	//	S_TEST = setupShader("test.vsh", "test.fsh");
	}
	
	/*public static void destroy() {
		destroyShader(S_TEST);
	}
	
	static int active = 0;
	public static void use(int prog) {
		active = prog;
		ARBShaderObjects.glUseProgramObjectARB(active);
		if(active != S_NONE) {
			int uniform = ARBShaderObjects.glGetUniformLocationARB(active, "timeMs");
			float a = (float)(System.currentTimeMillis() % 1000) / 1000f;
			ARBShaderObjects.glUniform1fARB(uniform, a);
			uniform = ARBShaderObjects.glGetUniformLocationARB(active, "resolution");
			ARBShaderObjects.glUniform2fARB(uniform, Client.mc.displayWidth, Client.mc.displayHeight);
		}
	}
	
	public static int setup(String path, int type) throws IOException {
		int s = ARBShaderObjects.glCreateShaderObjectARB(type);
		if(s == 0) return 0;
		
		String code = getCode(path);
		
		ARBShaderObjects.glShaderSourceARB(s, code);
		ARBShaderObjects.glCompileShaderARB(s);
		checkError(s);
		
		return s;
	}
	
	public static void checkError(int obj) {
		if (ARBShaderObjects.glGetObjectParameteriARB(obj, ARBShaderObjects.GL_OBJECT_COMPILE_STATUS_ARB) == GL11.GL_FALSE) {
			System.out.println("error in "+obj+" shader: "+getErr(obj));
		}
	}
	
	public static String getErr(int obj) {
		return ARBShaderObjects.glGetInfoLogARB(obj, ARBShaderObjects.glGetObjectParameteriARB(obj, ARBShaderObjects.GL_OBJECT_INFO_LOG_LENGTH_ARB));
	}
	
	public static void destroyShader(int prog) {
		ARBShaderObjects.glDeleteObjectARB(prog);
	}
	
	public static int setupShader(String vert, String frag) {
		int prog = ARBShaderObjects.glCreateProgramObjectARB();
		try {
			int v = 0, f = 0;
			if(prog != 0) {
				v = setup(vert, ARBVertexShader.GL_VERTEX_SHADER_ARB);
				f = setup(frag, ARBFragmentShader.GL_FRAGMENT_SHADER_ARB);
			}
			
			if(v != 0 || f != 0) {
				if(v != 0) ARBShaderObjects.glAttachObjectARB(prog, v);
				if(f != 0) ARBShaderObjects.glAttachObjectARB(prog, f);
				
				ARBShaderObjects.glLinkProgramARB(prog);
				
				if (ARBShaderObjects.glGetObjectParameteriARB(prog, ARBShaderObjects.GL_OBJECT_LINK_STATUS_ARB) == GL11.GL_FALSE) {
		            System.out.println(getErr(prog));
		            throw new Exception();
		        }
				
				System.out.println("bbbbbb");
		        ARBShaderObjects.glValidateProgramARB(prog);
		        if (ARBShaderObjects.glGetObjectParameteriARB(prog, ARBShaderObjects.GL_OBJECT_VALIDATE_STATUS_ARB) == GL11.GL_FALSE) {
		            System.out.println(getErr(prog));
		            throw new Exception();
		        }
			}else if(prog != 0) {
				ARBShaderObjects.glDeleteObjectARB(prog);
				prog = 0;
			}
			
			return prog;
		}catch(Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	public static String getCode(String path) throws IOException {
		BufferedReader reader;
		InputStream is = Minecraft.class.getResourceAsStream("/shader/"+path);
		reader = new BufferedReader(new InputStreamReader(is));
		String line;
		String code = "";
		while((line = reader.readLine()) != null) {
			code += line + "\n";
		}
		reader.close();
		return code;
	}*/
}
