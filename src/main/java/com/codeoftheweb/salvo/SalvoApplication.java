
package com.codeoftheweb.salvo;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.List;






@SpringBootApplication
public class SalvoApplication extends SpringBootServletInitializer {
	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class, args);
	}

    @Bean
        public PasswordEncoder passwordEncoder() {
	        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
        public Cache createDBCache () {
                return new Cache();
        }

	@Bean
	public CommandLineRunner initData(PlayerRepository playerRepository,
                                      GameRepository gameRepository,
                                      GamePlayerRepository gamePlayerRepository,
                                      ShipRepository shipRepository,
                                      SalvoRepository salvoRepository,
                                      ScoreRepository scoreRepository) {
		return (args) -> {

            // some ships
//            String tiger = "Tiger";
//		    String tinanic = "Titanic";
//		    String bombarder = "Bombarder";
//		    String union = "Union";
//		    String dipper = "Dipper";
//		    String fighter = "Fighter";


            Player p1 = new Player("Paco", "Steward", "crazyguy@hotestmail.com" , "909090");
            Player p2 = new Player("Hernando","Perez", "hernando@gmail.com" , "48947468784");
            Player p3 = new Player("Messi","Champion", "messiChampion@yahoo.es", "90u7747y4t4" );
            Player p4 = new Player("Peter", "Pan","peterPan@hotmail.com", "177457y77473");
            Player p5 = new Player("James", "Bond","jamesbond007@hotmail.com", "12345678");

            playerRepository.save( p1 );
            playerRepository.save( p2 );
            playerRepository.save( p3 );
            playerRepository.save( p4 );
            playerRepository.save( p5 );


            Game g1 = new Game();
            gameRepository.save( g1 );
            Game g2 = new Game();
            gameRepository.save( g2 );
            Game g3 = new Game();
            gameRepository.save( g3 );
            Game g4 = new Game();
            gameRepository.save( g4 );
            Game g5 = new Game();
            gameRepository.save( g5 );

//            final GamePlayer gp12 = new GamePlayer(p1, g1);
//            final GamePlayer gp22 = new GamePlayer(p2, g2);
//            final GamePlayer gp31 = new GamePlayer(p1, g3);
//            final GamePlayer gp13 = new GamePlayer(p4, g3);
//            final GamePlayer gp14 = new GamePlayer(p5, g3);
//            final GamePlayer gp33 = new GamePlayer(p4, g3);
//            final GamePlayer gp32 = new GamePlayer(p4, g3);
//            final GamePlayer gp11 = new GamePlayer(p1, g1);
//            final GamePlayer gp25 = new GamePlayer(p2, g2);
//            final GamePlayer gp23 = new GamePlayer(p4, g3);
//            final GamePlayer gp24 = new GamePlayer(p4, g3);
//            final GamePlayer gp35 = new GamePlayer(p3, g5);
//            final GamePlayer gp34 = new GamePlayer(p4, g3);
//            final GamePlayer gp15 = new GamePlayer(p4, g3);
//            final GamePlayer gp43 = new GamePlayer(p4, g3);
//            final GamePlayer gp45 = new GamePlayer(p4, g3);
//            final GamePlayer gp41 = new GamePlayer(p4, g3);
//
//
//            gamePlayerRepository.save(gp12);
//            gamePlayerRepository.save(gp22);
//            gamePlayerRepository.save(gp31);
//            gamePlayerRepository.save(gp13);
//            gamePlayerRepository.save(gp14);
//            gamePlayerRepository.save(gp33);
//            gamePlayerRepository.save(gp32);
//            gamePlayerRepository.save(gp12);
//            gamePlayerRepository.save(gp22);
//            gamePlayerRepository.save(gp31);
//            gamePlayerRepository.save(gp13);
//            gamePlayerRepository.save(gp14);
//            gamePlayerRepository.save(gp34);
//            gamePlayerRepository.save(gp15);
//            gamePlayerRepository.save(gp41);
//            gamePlayerRepository.save(gp45);
//            gamePlayerRepository.save(gp43);

            final GamePlayer gp11 = new GamePlayer(p1, g2);
            gamePlayerRepository.save( gp11 );
            final GamePlayer gp12 = new GamePlayer(p2, g2);
            gamePlayerRepository.save( gp12 );
            final GamePlayer gp13 = new GamePlayer(p3, g1);
            gamePlayerRepository.save( gp13 );
            final GamePlayer gp14 = new GamePlayer(p4, g1);
            gamePlayerRepository.save( gp14 );
            final GamePlayer gp15 = new GamePlayer(p3, g4);
            gamePlayerRepository.save( gp15 );
            final GamePlayer gp16 = new GamePlayer(p2, g4);
            gamePlayerRepository.save( gp16 );
            final GamePlayer gp17 = new GamePlayer(p1, g3);
            gamePlayerRepository.save( gp17 );
            final GamePlayer gp18 = new GamePlayer(p3, g3);
            gamePlayerRepository.save( gp18 );
            final GamePlayer gp19 = new GamePlayer(p2, g4);
            gamePlayerRepository.save( gp19 );
            final GamePlayer gp20 = new GamePlayer(p3, g4);
            gamePlayerRepository.save( gp20 );
            final GamePlayer gp21 = new GamePlayer(p2, g5);
            gamePlayerRepository.save( gp21 );
            final GamePlayer gp22 = new GamePlayer(p5, g5);
            gamePlayerRepository.save( gp22 );
            final GamePlayer gp23 = new GamePlayer(p4, g5);
            gamePlayerRepository.save( gp23 );
            final GamePlayer gp24 = new GamePlayer(p3, g5);
            gamePlayerRepository.save( gp24 );
            final GamePlayer gp25 = new GamePlayer(p2, g5);
            gamePlayerRepository.save( gp25 );
           final GamePlayer gp26 = new GamePlayer(p3, g5);
            gamePlayerRepository.save( gp26 );
            final GamePlayer gp27 = new GamePlayer(p2, g5);
            gamePlayerRepository.save( gp27 );
            final GamePlayer gp28 = new GamePlayer(p1, g3);
            gamePlayerRepository.save( gp28 );
            final GamePlayer gp29 = new GamePlayer(p4, g3);
            gamePlayerRepository.save( gp29 );

            List<String> loc1 = Arrays.asList("A1","A2","A3");
            List<String> loc2 = Arrays.asList("E1","E2","E3");
            List<String> loc3 = Arrays.asList("C1","C2","C3");
            List<String> loc4 = Arrays.asList("D1","D2","D3","D4");
            List<String> loc5 = Arrays.asList("H3","H4","H5");
            List<String> loc6 = Arrays.asList("B1","B2","B3");
            List<String> loc7 = Arrays.asList("D1","D2","B3");
            List<String> loc8 = Arrays.asList("G1","G2","G3");
            List<String> loc9 = Arrays.asList("D4","D5","D6","D7");
            List<String> loc10 = Arrays.asList("H3","H4","H5", "H6");


            Ship hunter = new Ship("Hunter",loc3, gp12);
            Ship bigBoy = new Ship ("BigBoy", loc2, gp12);
            Ship terminator = new Ship ("Terminator",loc5, gp13);
            Ship submarine = new Ship ("Submarine", loc5, gp12);
            Ship depredator = new Ship ("Depredator",loc8, gp13);
            Ship cruiser = new Ship("Cruiser",loc1, gp11);
            Ship titanic = new Ship("Titanic",loc7, gp14);
            Ship bigWarrior = new Ship("BigWarrior",loc1, gp11);
            Ship corvetta = new Ship ("Corvetta", loc1, gp11);
            Ship saveNavy = new Ship ("SaveNavy",loc9, gp11);


            shipRepository.save(hunter);
            shipRepository.save(bigBoy);
            shipRepository.save(terminator);
            shipRepository.save(submarine);
            shipRepository.save(depredator);

            shipRepository.save(cruiser);
            shipRepository.save(titanic);
            shipRepository.save(bigWarrior);
            shipRepository.save(corvetta);
            shipRepository.save(saveNavy);


            Ship Panther = new Ship("Panther",loc3,gp13);
            Ship Poseidon = new Ship("Poseidon",loc4,gp11);
            Ship Scott = new Ship("Scott",loc3,gp11);
            Ship Thunder = new Ship ("Thunder", loc3,gp11);
            Ship HMS_Attacker = new Ship ("HMS_Attacker",loc3,gp12);

            shipRepository.save(Panther);
            shipRepository.save(Poseidon);
            shipRepository.save(Scott);
            shipRepository.save(Thunder);
            shipRepository.save(HMS_Attacker);


            Ship Tiger = new Ship("Tiger",loc4, gp14);
            Ship Venus = new Ship("Venus",loc5,gp14);
            Ship Dipper = new Ship("Dipper",loc9,gp15);
            Ship Surprise = new Ship ("Surprise", loc6,gp15);
            Ship OldNavy = new Ship ("OldNavy",loc10, gp11);

            shipRepository.save(Tiger);
            shipRepository.save(Venus);
            shipRepository.save(Dipper);
            shipRepository.save(Surprise);
            shipRepository.save(OldNavy);


            List<String> salvoLoc111 = Arrays.asList("E4", "B5", "B6");
            List<String> salvoLoc121 = Arrays.asList("B4", "B5", "B6");
            List<String> salvoLoc112 = Arrays.asList("F2", "D5", "C6");
            List<String> salvoLoc122 = Arrays.asList("E1", "H3", "A2");

            List<String> salvoLoc211 = Arrays.asList("A2", "A4", "G6");
            List<String> salvoLoc221 = Arrays.asList("B5", "D5", "C7");
            List<String> salvoLoc212 = Arrays.asList("A3", "H6", "B8");
            List<String> salvoLoc222 = Arrays.asList("D1", "D2", "B5");

            List<String> salvoLoc311 = Arrays.asList("G6", "H6", "A4");
            List<String> salvoLoc321 = Arrays.asList("H1", "H2", "H3");
            List<String> salvoLoc312 = Arrays.asList("A2", "A3", "D8");
            List<String> salvoLoc322 = Arrays.asList("E1", "F2", "G3");

            List<String> salvoLoc411 = Arrays.asList("A3", "A4", "F7");
            List<String> salvoLoc421 = Arrays.asList("B5", "C6", "H1");
            List<String> salvoLoc412 = Arrays.asList("A2", "G6", "H6");
            List<String> salvoLoc422 = Arrays.asList("C5", "C7", "D5");

            List<String> salvoLoc511 = Arrays.asList("A1", "A2", "A3");
            List<String> salvoLoc521 = Arrays.asList("B5", "B6", "C7");
            List<String> salvoLoc512 = Arrays.asList("G6", "G7", "G8");
            List<String> salvoLoc522 = Arrays.asList("C6", "D6", "E6");
            List<String> salvoLoc523 = Arrays.asList("H1", "H8", "E5");

            Salvo salvo111 = new Salvo(1, salvoLoc111, gp11);
            Salvo salvo112 = new Salvo(2, salvoLoc112, gp12);
            Salvo salvo121 = new Salvo(1, salvoLoc121, gp13);
            Salvo salvo122 = new Salvo(2, salvoLoc122, gp14);
            Salvo salvo211 = new Salvo(1, salvoLoc211, gp15);
            Salvo salvo212 = new Salvo(2, salvoLoc212, gp14);
            Salvo salvo221 = new Salvo(1, salvoLoc221, gp24);
            Salvo salvo222 = new Salvo(2, salvoLoc222, gp15);
            Salvo salvo311 = new Salvo(4, salvoLoc311, gp13);
            Salvo salvo312 = new Salvo(2, salvoLoc312, gp12);
            Salvo salvo321 = new Salvo(3, salvoLoc321, gp15);
            Salvo salvo322 = new Salvo(2, salvoLoc322, gp22);
            Salvo salvo411 = new Salvo(1, salvoLoc411, gp23);
            Salvo salvo412 = new Salvo(2, salvoLoc412, gp24);
            Salvo salvo421 = new Salvo(1, salvoLoc421, gp25);
            Salvo salvo422 = new Salvo(2, salvoLoc422, gp11);
            Salvo salvo511 = new Salvo(1, salvoLoc511, gp12);
            Salvo salvo512 = new Salvo(2, salvoLoc512, gp12);
            Salvo salvo521 = new Salvo(1, salvoLoc521, gp24);
            Salvo salvo522 = new Salvo(2, salvoLoc522, gp24);
            Salvo salvo523 = new Salvo(5, salvoLoc523, gp22);

            salvoRepository.save(salvo111);
            salvoRepository.save(salvo112);
            salvoRepository.save(salvo121);
            salvoRepository.save(salvo122);
            salvoRepository.save(salvo211);
            salvoRepository.save(salvo212);
            salvoRepository.save(salvo221);
            salvoRepository.save(salvo222);
            salvoRepository.save(salvo311);
            salvoRepository.save(salvo312);
            salvoRepository.save(salvo321);
            salvoRepository.save(salvo322);
            salvoRepository.save(salvo411);
            salvoRepository.save(salvo412);
            salvoRepository.save(salvo421);
            salvoRepository.save(salvo422);
            salvoRepository.save(salvo511);
            salvoRepository.save(salvo512);
            salvoRepository.save(salvo521);
            salvoRepository.save(salvo522);
            salvoRepository.save(salvo523);


            Score score12 = new Score(g3, p3, 1.0 );
            Score score13 = new Score(g2, p2, 0.5 );
            Score score14 = new Score(g1, p1, 0.0 );
            Score score15 = new Score(g3, p2, 1.0 );
            Score score16 = new Score(g2, p1, 1.0 );
            Score score17 = new Score(g4, p2, 0.0 );
            Score score18 = new Score(g5, p3, 0.5 );
            Score score19 = new Score(g1, p4, 0.5 );
            Score score20 = new Score(g5, p2, 1.0 );
            Score score21 = new Score(g1, p4, 1.0 );


            scoreRepository.save(score12);
            scoreRepository.save(score13);
            scoreRepository.save(score14);
            scoreRepository.save(score15);
            scoreRepository.save(score16);
            scoreRepository.save(score17);
            scoreRepository.save(score18);
            scoreRepository.save(score19);
            scoreRepository.save(score20);
            scoreRepository.save(score21);

		};
	}
}

@Configuration
class MvcConfig extends WebMvcConfigurerAdapter {
    @Override
        public void addViewControllers(ViewControllerRegistry registry) {
                registry.addViewController("/").setViewName("web/games.html");
        }
}


@Configuration
class WebSecurityConfiguration extends GlobalAuthenticationConfigurerAdapter {

        @Autowired
        PlayerRepository playerRepository;


        @Override
        public void init(AuthenticationManagerBuilder auth) throws Exception {
                auth.userDetailsService(userDetailsService());
        }

        @Bean
        UserDetailsService userDetailsService() {
                return new UserDetailsService() {
                        @Override
                        public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                                Player player = playerRepository.findByUserName(username);
                                if (player != null) {
                                        return new User(player.getUserName(), player.getPassword(),
                                                AuthorityUtils.createAuthorityList("USER"));
                                } else {
                                        throw new UsernameNotFoundException("Unknown user: " + username);
                                }
                        }
                };
        }

}

        @EnableWebSecurity
        @Configuration
        class WebSecurityConfig extends WebSecurityConfigurerAdapter {
                @Override
                protected void configure(HttpSecurity http) throws Exception {
                        http.authorizeRequests()
                                .antMatchers("/**").permitAll()
//                                .antMatchers("/api/games").permitAll()
//                                .antMatchers("/api/players").permitAll()
//                                .antMatchers("/web/games.html").permitAll()
//                                .antMatchers("/web/script/games.js").permitAll()
//                                .antMatchers("/web/style/games.css").permitAll()
                                .antMatchers("/rest/**").denyAll();
//                                .anyRequest().fullyAuthenticated();

                        http.formLogin()
                                .usernameParameter("userName")
                                .passwordParameter("password")
                                .loginPage("/api/login");

                        http.logout().logoutUrl("/api/logout");

                        // turn off checking for CSRF tokens
                        http.csrf().disable();

                        // if user is not authenticated, just send an authentication failure response
                        http.exceptionHandling()
                                .authenticationEntryPoint((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

                        // if login is successful, clear the flags asking for authentication
                        http.formLogin().successHandler((req, res, auth) -> clearAuthenticationAttributes(req));

                        // if login fails, just send an authentication failure response
                        http.formLogin().failureHandler((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

                        // if logout is successful, just send a success response
                        http.logout().logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler());
                }

                private void clearAuthenticationAttributes(HttpServletRequest request) {
                        HttpSession session = request.getSession(false);
                        if (session != null) {
                                session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
                        }
                }
}




