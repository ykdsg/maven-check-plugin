package com.taobao.ju.maven;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.util.DirectoryScanner;


/**
 * 
 */
public abstract class AbstractScmConfigMojo extends AbstractMojo {
	

	public static final String SPT= System.getProperty("file.separator");


	/**
	 * The list of resources we want to transfer.
	 * 
	 * @parameter expression="${resources}"
	 */
	protected Resource[] resources;
	/**
	 * The list of resources we want to transfer.
	 * 
	 * @parameter expression="${project.resources}"
	 * @required
	 * @readonly
	 */
	protected List defaultResources;
	
	/**
	 * Location of the file.
	 * 
	 * @parameter expression="${project.build.directory}"
	 * @required
	 */
	protected File outputDirectory;
	/**
	 * Location of the file.
	 * 
	 * @parameter expression="${basedir}"
	 * @required
	 */
	protected File basedir;
    /**
     * The list of additional key-value pairs aside from that of the System,
     * and that of the project, which would be used for the filtering.
     *
     * @parameter expression="${project.build.filters}"
     */
    protected List filters;
	
	protected boolean hasError =false;
	
	protected boolean isCheck =true;
	
	protected boolean hasChange = false;

	protected static final String REGEX = "\\$\\{[\\w*|\\.]*\\}";

	protected static final Pattern PATTERN = Pattern.compile(REGEX);
	
	protected Set<String> vars = new HashSet<String>();
	
	//EXCEPTION_PATTERN.matcher(oneLog.get(i)).find()
	
	public void execute() throws MojoExecutionException {
		getLog().info("	ƽ̨�ܹ��������ĳ�Ʒ����ӭʹ�ã� ");
		if((resources==null||resources.length<1) && defaultResources!=null){
			this.getLog().info("	����Ƿ���Resouces�����ã�");
			resources = new Resource[defaultResources.size()];
			defaultResources.toArray(resources);
		}
		if(resources==null ||!isFilteringEnabled(resources)){
			this.getLog().info("	û�з�����Ҫ���˵�Resources���ã�������");
			return;
		}
		//����ļ����Ƿ����û�б����˵�
		if(isCheck){
			getLog().info("	��ʼУ���������Ƿ��Ѿ������壡 ");
		}else{
			getLog().info("	��ʼ��������Ѵ��ڵ������ ");
		}
		for (Resource resource: resources) {
			if(!resource.isFiltering()){
				getLog().warn( resource.getDirectory());
				continue;	
			}
			String outDir = outputDirectory.toString()+SPT+"classes";
			if(resource.getTargetPath()!=null){
				outDir = basedir.toString()+SPT+resource.getTargetPath();
			}
			File sDir = new File(resource.getDirectory());

			if(!(sDir.exists()&&sDir.isDirectory())){
				sDir = new File(basedir.toString()+SPT+resource.getDirectory());
				if(!(sDir.exists()&&sDir.isDirectory())){
					this.getLog().warn("	�����Դ�ļ�Ŀ¼�����ڣ�"+sDir.toString());
					continue;
				}
			}
			try {
				checkFiles(sDir.toString(),outDir,isCheck);
			} catch (IOException e) {
				throw new MojoExecutionException(e.toString(),e);
			}
		}
		if(hasChange){
			this.savePorpToFile();
		}
		if(isCheck && hasError){
			throw new MojoExecutionException("��������������У�������⣬���飡���ֻ��������������ɺ��ԣ���������");
		}
		if(!isCheck&&vars.size()>0){
			this.getLog().info("	����Ŀ�г��ֹ��ı����У�value����properties�ļ������õ�����ֵ����");
			for(String str:vars){
				outPorpAndValue(str);
			}
		}
		
	}

	protected void checkFiles(String sourceDir,String outDir ,boolean isCheck) throws IOException{
	        DirectoryScanner resourceScanner = new DirectoryScanner();
	        resourceScanner.setBasedir( sourceDir);
	        resourceScanner.setExcludes(DirectoryScanner.DEFAULTEXCLUDES);
	        resourceScanner.scan();
	        String[] includedFiles = resourceScanner.getIncludedFiles();
	
	        for ( int i = 0;  i < includedFiles.length; i++)
	        {
	            String name = includedFiles[i];

            	this.getLog().info("	����ļ���"+name);
//	            File destinationFile = isCheck?new File( outDir, name):new File( sourceDir, name);
	            //ȫ�����ԭ�ļ�
            	File destinationFile = new File( sourceDir, name);
	            if(destinationFile.exists() && destinationFile.isFile()){
	            	checkVar(destinationFile,isCheck);
	            }else{
					this.getLog().warn("	���Ŀ���ļ������ڣ�"+destinationFile.toString());
	            }

	        }
	}
	protected void checkVar(File file,boolean isCheck) throws IOException{
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		// �Ժ���Կ��ƿ�ʼline�ͽ���line
		String line;
		long count=1;
		while ((line = br.readLine()) != null) {
			Matcher match = PATTERN.matcher(line);
			while(match.find()){
				if(isCheck){
					String resstr =match.group();
					String tPropKey = getPropKey(resstr);
					if(this.getValue(tPropKey)==null ){
						this.getLog().info("	Line "+count+":"+resstr);
						if(!inputKeyAndValue(tPropKey)){
							hasError=true;
						}
					}
				}else{
					String resstr =match.group();
					vars.add(resstr);
					checkLength(resstr);
					this.getLog().info("		Line "+count+":"+resstr);
				}
			}
			count++;
		}
		br.close();
		fr.close();
	}
	/**
	 * ���û�����û��ȷ�ϵ�Key��Value
	 * @param propKey
	 */
	protected boolean inputKeyAndValue(String propKey){
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		System.out.print("[INFO] 	��������\""+propKey+"\"��ֵδ���壬�����룺");
		try {
			String str = br.readLine();
			this.saveValue(propKey,str);
			getLog().error("����:"+str);
			return true;
		} catch (IOException e) {
			getLog().error("�������",e);
			return false;
		}
	}

	protected int maxLength =30;
	protected void checkLength(String str){
		if(null!=str&&str.length()>maxLength){
			maxLength = str.length();
		}
	}
	protected String getPropKey(String propKey){
		if(propKey==null)return null;
		return propKey.substring(2, propKey.length()-1);
	}
	protected void outPorpAndValue(String propKey){
		if(propKey==null)return;
		String outstr = "	��"+propKey;
		String value = this.getValue(getPropKey(propKey));
		if(value!=null){
			for(int i=propKey.length();i<maxLength+5;i++){
				outstr +=" ";
			}
			outstr += "vlaue="+value;
		}
		this.getLog().info(outstr);
		
	}
	protected List<Properties> props=null; 
	protected Properties firstProp = null;
	protected String firstPropFileName = null;
	protected String getValue(String key){
		if(key==null)return null;
		if(props==null){
			props = new ArrayList<Properties>();
			if(filters!=null && filters.size()>0){
				for(Iterator<String> it=filters.iterator();it.hasNext();){
					String str = it.next();
					Properties ppr = new Properties();
					try {
						ppr.load(new FileReader(new File(str)));
						if(firstProp==null){
							firstProp = ppr;
							firstPropFileName = str;
						}
						props.add(ppr);
					} catch (Exception e) {
						this.getLog().error("������Դ�ļ������ļ�����"+str);
					}
				}
			}
		}
		for(Properties prop:props){
			if(prop.getProperty(key)!=null){
				return prop.getProperty(key);
			}
		}
		return null;
	}
	protected boolean saveValue(String key,String value){
		if(firstProp==null || firstPropFileName == null) return false;
		firstProp.setProperty(key, value);
		hasChange = true;
		return true;
	}
	protected boolean savePorpToFile(){
		if(firstProp==null || firstPropFileName == null) return false;
		try {
			firstProp.store(new FileWriter(firstPropFileName),"");
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
/*
 * 
		if(filters!=null && filters.size()>0){
			this.getLog().info("�����ļ�:"+filters.get(0).toString());
			this.getLog().info("�����ļ�:"+filters.get(0).getClass().getName());
		}
 * */
	protected boolean isFilteringEnabled(Resource[] resources) {
		if (resources != null) {
			for (Resource resource : resources) {
				if (resource.isFiltering()) {
					return true;
				}
			}
		}
		return false;
	}


	public File getOutputDirectory() {
		return outputDirectory;
	}

	public void setOutputDirectory(File outputDirectory) {
		this.outputDirectory = outputDirectory;
	}

	public File getBasedir() {
		return basedir;
	}

	public void setBasedir(File basedir) {
		this.basedir = basedir;
	}

	public Resource[] getResources() {
		return resources;
	}

	public void setResources(Resource[] resources) {
		this.resources = resources;
	}

	public List getFilters() {
		return filters;
	}

	public void setFilters(List filters) {
		this.filters = filters;
	}




}
