package net.genebolt.webserv.controller;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import net.genebolt.webserv.exception.InvalidUserRequestException;
import net.genebolt.webserv.exception.UserNotFoundException;
import net.genebolt.webserv.model.UserImage;
import net.genebolt.webserv.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import net.genebolt.webserv.model.User;
import net.genebolt.webserv.service.ImageService;

/**
 * User Controller exposes a series of RESTful endpoints
 */
@RestController
public class UserController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ImageService imageService;

	@RequestMapping(value = "/users", method = RequestMethod.POST)
    public @ResponseBody User createUser(
            @RequestParam(value="firstName", required=true) String firstName,
            @RequestParam(value="lastName", required=true) String lastName,
			@RequestParam(value="description", required=true) String description,
            @RequestParam(value="image", required=true) MultipartFile image) {

        	UserImage userImage = imageService.saveImageToS3(image);
        	User user = new User(firstName, lastName, description, userImage);

        	userRepository.save(user);
            return user;
    }

	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public @ResponseBody User updateUser(
			@RequestParam(value="uid", required=true) Long uid,
			@RequestParam(value="firstName", required=true) String firstName,
			@RequestParam(value="lastName", required=true) String lastName,
			@RequestParam(value="description", required=true) String description,
			@RequestParam(value="image", required=true) MultipartFile image,
			@RequestParam(value="iid", required=true) Long iid,
			@RequestParam(value="key", required=true) String key,
			@RequestParam(value="url", required=true) String url) {
		UserImage userImage = imageService.updateImageFromS3(iid, key, url, image);
		User user = new User(uid, firstName, lastName, description, userImage);

		userRepository.save(user);
		return user;
	}

	/**
	 * Get user using id.
	 *
	 * @param userId
	 * @return retrieved user
	 */
	@RequestMapping(value = "/users/{userId}", method = RequestMethod.GET)
	public User getUser(@PathVariable("userId") Long userId) {

		/* validate user Id parameter */
		if (null==userId) {
			throw new InvalidUserRequestException();
		}

		User user = userRepository.findOne(userId);

		if(null==user){
			throw new UserNotFoundException();
		}

		return user;
	}

	/**
	 * Gets all users.
	 *
	 * @return the users
	 */
	@RequestMapping(value = "/users", method = RequestMethod.GET)
	public List<User> getUsers() {

		return (List<User>) userRepository.findAll();
	}

	/**
	 * Deletes the user with given user id if it exists.
	 *
	 * @param userId the user id
	 */
	@RequestMapping(value = "/users/{userId}", method = RequestMethod.DELETE)
	public void removeUser(@PathVariable("userId") Long userId, HttpServletResponse httpResponse) {

		if(userRepository.exists(userId)){
			User user = userRepository.findOne(userId);
			imageService.deleteImageFromS3(user.getUserImage());
			userRepository.delete(user);
		}

		httpResponse.setStatus(HttpStatus.NO_CONTENT.value());
	}

}