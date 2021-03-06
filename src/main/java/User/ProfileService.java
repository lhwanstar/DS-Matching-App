package User;

import Common.Result;
import Config.TokenProvider;
import Team.Role;
import kr.ac.konkuk.ccslab.cm.info.CMInfo;

public class ProfileService {
    private CMInfo cmInfo;
    private ProfileRepository profileRepository;
    
    public ProfileService(CMInfo cmInfo) {
        this.cmInfo = cmInfo;
        this.profileRepository = new ProfileRepository();
    }
	
    public Profile getProfile(Long userId, Result result) {
    	return profileRepository.getProfileByUserId(userId, result, cmInfo);
    }
    
    public Profile postProfile(
    		TokenProvider.TokenResult validResult, 
    		Result result, Role role, String content, 
    		String photo, String portforlio, String originalPortfolio, String fileName, String originalFileName) {

        User user = new User.Builder()
                .id(validResult.getId())
                .build();

        Profile profile = new Profile.Builder()
        		.user(user)
        		.role(role)
        		.content(content)
        		.photo(photo)
        		.portforlio(portforlio)
        		.fileName(fileName)
				.originalPortforlio(originalPortfolio)
        		.originalFileName(originalFileName)
        		.build();

        profileRepository.postProfile(profile, result, cmInfo);
    	return profile;
    }
    
    public Long putProfile(Profile profile, Result result, 
    		Role role, String content, String photo,
    		String portforlio, String fileName, String originalFileName) {
    	
    	profile.setRole(role);
    	profile.setContent(content);
    	profile.setPhoto(photo);
    	profile.setPortforlio(portforlio);
    	profile.setFileName(fileName);
    	profile.setOriginalFileName(originalFileName);
        return profileRepository.putProfile(profile, result, cmInfo);
    }
    
    public void deleteProfile(Long userId, Result result) {
    	profileRepository.deleteProfile(userId, result, cmInfo);
    }


}
