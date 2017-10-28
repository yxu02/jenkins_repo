package net.genebolt.webserv.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import lombok.Getter;
import lombok.Setter;

@Entity(name="app_user")
public class User {

	public User(){}
	
	public User(String firstName, String lastName, String desc, UserImage userImage) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.description = desc;
		this.userImage = userImage;
	}
	public User(Long uid, String firstName, String lastName, String desc, UserImage userImage) {
		this.id = uid;
		this.firstName = firstName;
		this.lastName = lastName;
		this.description = desc;
		this.userImage = userImage;
	}

	@Id
	@Getter
    @GeneratedValue(strategy=GenerationType.AUTO)
    private long id;
	
	@Setter
	@Getter
	@Column(nullable = false, length = 40)
	private String firstName;
	
	@Setter
	@Getter
	@Column(nullable = false, length = 40)
	private String lastName;
	
	@Setter	
	@Getter
	@Column(nullable = false, length = 100)
	private String description;
	
	@Setter
	@Getter
	@OneToOne(cascade = {CascadeType.ALL})
	private UserImage userImage;

	public UserImage getUserImage() {
		return userImage;
	}
}