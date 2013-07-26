package daniel;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertThat;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

public class TxtMainTest {

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@Test
	public void deveLerArquivoTxtNaPastaRaizEGerarArquivoCustomizado() throws Exception {
		File newFolder = folder.newFolder();

		TxtMain txtMain = new TxtMain(newFolder, "src/test/resources/arqcon.TXT");
		File newFile = new File(newFolder, txtMain.getFileName());
		
		assertThat(FileUtils.readFileToString(newFile), is(FileUtils.readFileToString(new File("src/test/resources/arquivo_customizado_correto"))));
	}

	@Test
	public void deveLerVariosArquivosTxtsEGerarArquivoCustomizado() throws Exception {
		File newFolder = folder.newFolder();

		TxtMain txtMain = new TxtMain(newFolder, "src/test/resources");
		File newFile = new File(newFolder, txtMain.getFileName());
		
		assertThat(FileUtils.readFileToString(newFile), containsString("7622300359768000033,00"));
	}
	
	@Test
	public void deveLancarExcecaoCasoCampoDoArgumentoSejaVazio() throws Exception {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Campo path() nao tem um caminho valido");
		
		new TxtMain("");
	}

	@Test
	public void deveLancarExcecaoCasoCampoDoArgumentoSejaNull() throws Exception {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Campo path nao pode ser vazio");
		
		new TxtMain();
	}
}
