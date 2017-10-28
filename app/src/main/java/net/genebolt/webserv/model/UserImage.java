package net.genebolt.webserv.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity(name="app_user_image")
public class UserImage {

	public UserImage(){}
	
	public UserImage(String key, Date uploadTime, Date updateTime, String url) {
		this.key = key;
		this.uploadTime = uploadTime;
		this.updateTime = updateTime;
		this.url =url;		
	}

	public UserImage(Long iid, String key, Date uploadTime, Date updateTime, String url) {
		this.id = iid;
		this.key = key;
		this.uploadTime = uploadTime;
		this.updateTime = updateTime;
		this.url =url;
	}

	@Id
	@Getter
    @GeneratedValue(strategy=GenerationType.AUTO)
    private long id;
	
	@Setter
	@Getter
	@Column(name = "s3Key", nullable = false)
	private String key;

	@Setter
	@Getter
	@Column(name = "uploadTime", nullable = false)
	private Date uploadTime;

	@Setter
	@Getter
	@Column(name = "updateTime", nullable = false)
	private Date updateTime;
	
	@Setter
	@Getter
	@Column(name = "url", nullable = false, length=1000)
	private String url;

	public String getKey() {
		return key;
	}

	public Date getUploadTime() {
		return uploadTime;
	}

	public String getUrl() {
		return url;
	}
}