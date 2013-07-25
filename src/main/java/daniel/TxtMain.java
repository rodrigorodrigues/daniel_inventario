package daniel;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

public class TxtMain {

	private Map<String, List<Double>> map = new HashMap<String, List<Double>>();
	
	private File fileRoot;
	
	private String fileName;
	
	public static void main(String[] args) {
		try {
			if (ArrayUtils.isEmpty(args)) {
				throw new IllegalArgumentException("Campo path nao pode ser vazio");
			}
			new TxtMain(args);
		} catch (IOException e) {
			System.out.println(e.getLocalizedMessage());
			if (StringUtils.contains(StringUtils.join(args), "--debug")) {
				e.printStackTrace();
			}
		}
	}
	
	public TxtMain(String ... paths) throws IOException {
		montaTxtCustomizado(paths);
	}

	public TxtMain(File fileRoot, String ... path) throws IOException {
		this.fileRoot = fileRoot;
		montaTxtCustomizado(path);
	}
	
	private List<File> getFilesFromDirectory(File file) {
		List<File> listFile = new ArrayList<File>();
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (File fileIterate : files) {
				if (fileIterate.isDirectory()) {
					List<File> filesRecursive = getFilesFromDirectory(fileIterate);
					if (CollectionUtils.isNotEmpty(filesRecursive)) {
						listFile.addAll(filesRecursive);
					}
				} else if (StringUtils.endsWithIgnoreCase(fileIterate.getName(), ".txt")) {
					listFile.add(fileIterate);
				}
			}
		}
		return listFile;
	}
	
	private File[] getFiles(String ... paths) {
		List<File> listFile = new ArrayList<File>();
	
		if (ArrayUtils.isEmpty(paths)) {
			throw new IllegalArgumentException("Campo path nao pode ser vazio");
		}
		
		for (String path : paths) {
		
			if ("--debug".equals(path))
				continue;
			
			File file = new File(path);
			if (!file.exists()) {
				throw new IllegalArgumentException("Campo path("+path+") nao tem um caminho valido");
			}
			
			if (!file.isDirectory() && StringUtils.endsWithIgnoreCase(file.getName(), ".txt")) {
				listFile.add(file);
			} else if (file.isDirectory()) {
				List<File> filesFromDirectory = getFilesFromDirectory(file);
				if (CollectionUtils.isNotEmpty(filesFromDirectory)) {
					listFile.addAll(filesFromDirectory);
				}
			}
			
		}
		
		return listFile.toArray(new File[]{});
	}
	
	private long getTimestamp() {
		return new Date().getTime();
	}
	
	private void montaTxtCustomizado(String ... paths) throws IOException {
		File[] files = getFiles(paths);
		for (File file : files) {
			geraListaDeProdutos(file);
		}
		
		if (map.isEmpty()) {
			throw new IllegalStateException("Nenhum produto foi encontrado no caminho: "+paths);
		} else {
			StringBuilder sb = new StringBuilder();
			for (Entry<String, List<Double>> entry : map.entrySet()) {
				sb.append(entry.getKey());
				String quantidadeProdutos = new DecimalFormat("#0.00").format(getQuantidadeProdutos(entry.getValue())).replaceFirst("\\.", ",");
				sb.append(StringUtils.leftPad(quantidadeProdutos, 9, "0"));
				sb.append(System.getProperty("line.separator"));
			}
			
			File file;
			fileName = "ARQUIVO_CUSTOMIZADO_"+getTimestamp()+".txt";
			if (fileRoot != null) {
				file = new File(fileRoot, fileName);
			} else {
				file = new File(fileName);
			}
			
			FileUtils.write(file, sb.toString());
			System.out.println("Arquivo gerado com sucesso!");
		}
	}
	
	private Double getQuantidadeProdutos(List<Double> list) {
		Double quantidade = new Double(0);
		for (Double doubleValue : list) {
			quantidade += doubleValue;
		}
		return quantidade;
	}
	
	private void geraListaDeProdutos(File file) throws IOException {
		List<String> lines = FileUtils.readLines(file, "utf-8");
		if (CollectionUtils.isNotEmpty(lines)) {
			for (String string : lines) {
				if (StringUtils.length(string) == 22) {
					String keyCodigoBarras = StringUtils.rightPad(string.substring(0, 13), 13, "") ;
					String replaceString = string.replaceFirst("\\,", ".");
					Double quantidade = new Double(replaceString.substring(13, replaceString.length()));
					
					List<Double> listQuantidade = map.get(keyCodigoBarras);
					if (listQuantidade == null) {
						listQuantidade = new ArrayList<Double>();
						map.put(keyCodigoBarras, listQuantidade);
					}
					listQuantidade.add(quantidade);
				}
			}
		}
	}
	
	public String getFileName() {
		return fileName;
	}
}
