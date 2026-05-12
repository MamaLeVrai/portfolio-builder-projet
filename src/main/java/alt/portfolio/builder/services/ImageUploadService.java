package alt.portfolio.builder.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImageUploadService {

	@Value("${app.upload.dir}")
	private String uploadDir;

	public String uploadProfilePhoto(MultipartFile file) throws IOException {
		return upload(file, "profiles");
	}

	public String uploadItemImage(MultipartFile file) throws IOException {
		return upload(file, "items");
	}

	private String upload(MultipartFile file, String subDir) throws IOException {
		if (file == null || file.isEmpty()) {
			throw new IllegalArgumentException("Fichier vide");
		}

		String contentType = file.getContentType();
		if (contentType == null || !contentType.startsWith("image/")) {
			throw new IllegalArgumentException("Seules les images sont autorisées");
		}

		String originalName = file.getOriginalFilename();
		String extension = "";
		if (originalName != null && originalName.contains(".")) {
			extension = originalName.substring(originalName.lastIndexOf("."));
		}
		String fileName = UUID.randomUUID() + extension;

		Path dir = Paths.get(uploadDir, subDir);
		Files.createDirectories(dir);

		Path dest = dir.resolve(fileName);
		file.transferTo(dest);

		return "/uploads/" + subDir + "/" + fileName;
	}
}
