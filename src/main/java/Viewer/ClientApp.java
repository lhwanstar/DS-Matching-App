package Viewer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import Team.Role;
import Team.Team;
import kr.ac.konkuk.ccslab.cm.entity.CMUser;
import kr.ac.konkuk.ccslab.cm.event.CMUserEvent;
import kr.ac.konkuk.ccslab.cm.info.CMInfo;
import kr.ac.konkuk.ccslab.cm.info.CMInteractionInfo;
import kr.ac.konkuk.ccslab.cm.manager.CMFileTransferManager;
import kr.ac.konkuk.ccslab.cm.stub.CMClientStub;
import mdlaf.MaterialLookAndFeel;
import mdlaf.themes.MaterialLiteTheme;



public class ClientApp extends JFrame {
	private static final long serialVersionUID = 1L;

	public CMClientStub clientStub;
    private ClientEventHandler clientEventHandler;
	public JLabel message = new JLabel("message will print here");
    public String token = null;
	
    private String email = null;
    private String pw = null;
    private TeamsView mainView = null;
    
    public Team my_team = null;
    private TeamCreateView teamCreateView;
    
	public ClientApp() {
		super("Team No3 - Matching System");
		setMinimumSize (new Dimension (UIConst.WIDTH, UIConst.HEIGHT));
		setResizable(false);
		setVisible (true);

		clientStub = new CMClientStub();
		clientEventHandler = new ClientEventHandler(this);
		clientStub.setAppEventHandler(clientEventHandler);
		
		this.addWindowListener(new java.awt.event.WindowAdapter() {
		    @Override
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
		        clientStub.terminateCM();
		        System.out.println("CM Terminated");
		    }
		});
		
		clientStub.startCM();
		print("message will print here");
		ChangeView(new LoginView(this));
		
		message.setBounds(30, 0, 500, 30);
	}
	
	public void print(String txt) {
		message.setText(txt);
	}
	
	public void exit() {
		clientStub.disconnectFromServer();
		System.exit(0);
	}
	
	public void requestConnection(String id, String pw) {
		this.email = id;
		this.pw = pw;
		
		if(this.email == null) {
			this.print("email is null");
			return;
		}
		if(this.pw == null) {
			this.print("password is null");
			return;
		}

		clientStub.loginCM(this.email, this.pw);
		System.out.println("requestConnection email : " + this.email);
		System.out.println("requestConnection pw : " + this.pw);
	}
	
	public void requestLogin() {
		CMUserEvent ue = new CMUserEvent();
		CMInteractionInfo info = clientStub.getCMInfo().getInteractionInfo();
		CMUser user = info.getMyself();
		ue.setStringID("SIGN-IN");
		
		System.out.println("requestLogin email : " + email);
		System.out.println("requestLogin pw : " + pw);
		ue.setEventField(CMInfo.CM_STR, "email", email);
		ue.setEventField(CMInfo.CM_STR, "password", pw);
		
		ue.setSender(user.getName());
		ue.setDistributionGroup(user.getCurrentGroup());
		ue.setDistributionSession(user.getCurrentSession());
		
		clientStub.send(ue, "SERVER");
		this.print("Request Login ...");
	}
	
	public void ChangeView(JPanel view) {
		this.getContentPane().removeAll();
		view.add(message);
		this.getContentPane().add(view, BorderLayout.CENTER);
		pack ();
	}
	
	public void requestSignUp(String name, String id, String pw, Role role, String introduce) {
		CMUserEvent ue = new CMUserEvent();
		CMInteractionInfo info = clientStub.getCMInfo().getInteractionInfo();
		CMUser user = info.getMyself();
		ue.setStringID("SIGN-UP");
		
		System.out.println("name : " + name);
		System.out.println("id : " + id);
		System.out.println("id : " + pw);
		
		ue.setEventField(CMInfo.CM_STR, "email", id);
		ue.setEventField(CMInfo.CM_STR, "password", pw);
		ue.setEventField(CMInfo.CM_STR, "username", name);
		
		
		ue.setEventField(CMInfo.CM_STR, "role", role.toString());
		ue.setEventField(CMInfo.CM_STR, "introduce", introduce);
		
		ue.setSender(user.getName());
		ue.setDistributionGroup(user.getCurrentGroup());
		ue.setDistributionSession(user.getCurrentSession());
		
		clientStub.send(ue, "SERVER");
		this.print("Requesting SignUp ...");
	}
	
	public void requestMyTeam(String team_name) {
		CMUserEvent ue = GetUE("GET-TEAM");
		ue.setEventField(CMInfo.CM_STR, "team_name", team_name);
		clientStub.send(ue, "SERVER");
		this.print("GET TEAM REQEUSTED");
	}
	private CMUserEvent GetUE(String id) {
		CMUserEvent ue = new CMUserEvent();
		CMInteractionInfo info = clientStub.getCMInfo().getInteractionInfo();
		CMUser user = info.getMyself();
		
		if(token == null) {
			System.out.println("FAIL FETCH TOKEN");
		}
		
		ue.setStringID(id);
		ue.setEventField(CMInfo.CM_STR, "token", this.token);
		ue.setSender(user.getName());
		ue.setDistributionGroup(user.getCurrentGroup());
		ue.setDistributionSession(user.getCurrentSession());
		return ue;
	}

	public void requestTeamList() {
		CMUserEvent ue = new CMUserEvent();
		CMInteractionInfo info = clientStub.getCMInfo().getInteractionInfo();
		CMUser user = info.getMyself();
		
		ue.setStringID("GET-TEAMS");
		ue.setEventField(CMInfo.CM_STR, "token", token);
		clientStub.send(ue, "SERVER");
		this.print("GET TEAM LIST (GET-TEAMS) REQEUSTED");
	}
	
	public void requestCreateTeam(Map<Role, Integer> limits) {
		CMUserEvent ue = new CMUserEvent();
		ue.setStringID("CREATE-TEAM");
		Map<Role, Integer> rolelimits = limits;
		//String json = client.objectMapper.writeValueAsString(rolelimits);

		//ue.setEventField(CMInfo.CM_STR, "team_name", "留뚮굹�꽌 諛섍��썙�슂 ^^");
		//ue.setEventField(CMInfo.CM_STR, "token", token);
		//ue.setEventField(CMInfo.CM_STR, "teamlimit", json);
	}
	
	public void applyTeam() {
	      CMUserEvent ue = new CMUserEvent();
	      CMInteractionInfo info = clientStub.getCMInfo().getInteractionInfo();
	      CMUser user = info.getMyself();
	      ue.setStringID("APPLY-TEAM");
	      
	      ue.setSender(user.getName());
	      ue.setDistributionGroup(user.getCurrentGroup());
	      ue.setDistributionSession(user.getCurrentSession());
	      
	      clientStub.send(ue, "SERVER");
	      this.print("Successfully applied ...");
	   }
	
	public static void main (String[] args) {
		try {
			UIManager.setLookAndFeel (new MaterialLookAndFeel ());
		}
		catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace ();
		}
		new ClientApp();
	}

	public void reqeustNewPost(Long team_id, String content) {
		CMUserEvent ue = GetUE("POST-BOARD");
		ue.setEventField(CMInfo.CM_LONG, "team_id", String.valueOf(team_id));
		ue.setEventField(CMInfo.CM_STR, "content", content);
		clientStub.send(ue, "SERVER");
	}

	public void requestApplications() {
		CMUserEvent ue = GetUE("GET-APPLICATIONS");
		clientStub.send(ue, "SERVER");
		print("GET-APPLICATIONS requested");
	}

	public void requestProcessApplication(String user_id, String team_id) {
		CMUserEvent ue = GetUE("PROCESS-APPLICATION");
		ue.setEventField(CMInfo.CM_LONG, "user_id", user_id);
		ue.setEventField(CMInfo.CM_LONG, "team_id", team_id);
		ue.setEventField(CMInfo.CM_INT, "yesTeam", "1");
		clientStub.send(ue, "SERVER");
	}

	public void reqeustMyTeam(String team_name) {
		CMUserEvent ue = GetUE("GET-TEAM");
		ue.setEventField(CMInfo.CM_STR, "team_name", team_name);
		clientStub.send(ue, "SERVER");
		this.print("GET TEAM REQEUSTED");
	}

	public void requestGetTeams() {
		// TODO Auto-generated method stub
		
	}

	public void requestLoginWithParam(String id, String pw2) {
		// TODO Auto-generated method stub
		CMUserEvent ue = new CMUserEvent();
		CMInteractionInfo info = clientStub.getCMInfo().getInteractionInfo();
		CMUser user = info.getMyself();
		ue.setStringID("SIGN-IN");

		ue.setEventField(CMInfo.CM_STR, "email", email);
		ue.setEventField(CMInfo.CM_STR, "password", pw);
		ue.setSender(user.getName());
		ue.setDistributionGroup(user.getCurrentGroup());
		ue.setDistributionSession(user.getCurrentSession());
		
		clientStub.send(ue, "SERVER");
		this.print("Request Login ...");
	}

	public void createProfileRequest(String role, String introduce, String photo_pathFileName,
			String photo_originalFileName, String portfolio_pathFileName, String portfolio_originalFileName) {
		
		CMUserEvent ue = new CMUserEvent();
		CMInteractionInfo info = clientStub.getCMInfo().getInteractionInfo();
		CMUser user = info.getMyself();
		ue.setStringID("POST-PROFIE");
		
		String portfolioInServer = "/" + user.getName() + "/" + portfolio_originalFileName;
		String imageInServer = "/" + user.getName() + "/" + photo_originalFileName;
		
		ue.setEventField(CMInfo.CM_STR, "role", role.toString());
		ue.setEventField(CMInfo.CM_STR, "content", introduce);
		ue.setEventField(CMInfo.CM_STR, "token", this.token);
		
		ue.setEventField(CMInfo.CM_STR, "file_name", imageInServer);
		ue.setEventField(CMInfo.CM_STR, "original_file_name", photo_originalFileName);
		ue.setEventField(CMInfo.CM_STR, "portfolio",portfolioInServer);
		ue.setEventField(CMInfo.CM_STR, "original_portfolio", portfolio_originalFileName);

		ue.setSender(user.getName());
		ue.setDistributionGroup(user.getCurrentGroup());
		ue.setDistributionSession(user.getCurrentSession());
		
		clientStub.send(ue, "SERVER");
		this.print("Request Login ...");
		

		CMFileTransferManager.pushFile(photo_pathFileName, "SERVER", clientStub.getCMInfo());
		CMFileTransferManager.pushFile(photo_pathFileName, "SERVER", clientStub.getCMInfo());
		
		
	}
}