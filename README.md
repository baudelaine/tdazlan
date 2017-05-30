# tdazlan
tdazlan bluemix demos

Install the cf CLI from here:
https://docs.cloudfoundry.org/cf-cli/install-go-cli.html

Install Git command line from here:
https://git-scm.com/downloads

//Connect to Bluemix (Dallas)
cf l -a https://api.ng.bluemix.net -u <userid> -p <password> --skip-ssl-validation -s <spave> -o <org which is usually the same as userid>

//Create cloudantNoSQLDB service
//cf cs <service> <plan> <service_instance>
cf cs cloudantNoSQLDB Lite db0

//Create service key (credential) to grant access to service
// cf csk <service_instance> <service_key>
cf csk db0 user0

//Now service key should be created. Check it with
//cf sk <service_instance>
cf sk db0

//Credential (url, port, username, password...) is available via
//cf service-key <service_instance> <service_key>
cf service-key db0 user0

//Now before create Twilio service instance, sign in https://www.twilio.com.
//Get Account_SID and a Auth_Token
//Add funds (20$ minimum)
//Buy a sms phone number (1$ for one month) located in Dallas: +1 (469) NNN-NNNN

//Then come back to cf cli and create Twilio service
//cf cups <service_instance> -p <parameters in json format>
cf cups sms0 -p '{"url":"https://api.twilio.com","accountSID":"AC...","authToken":"...","phones":"+1 469-NNN-NNNN"}'

//Check that both services are created
cf s

//Now download demo1 and demo2 code from github
git clone https://github.com/baudelaine/tdazlan.git

//Once cloning has finished change to local copy directory
cd tdazlan/

//If not already done, create a mybluemix.net subdomain in your org
//First list orgs to get yours
cf orgs
//Then create the subdomain
//cf create-domain <org> <subdomain> 
cf create-domain <org> tdazlan.mybluemix.net
//check domain exists
cf domains

//Now we are ready to deploy both demo1 and demo2 applications

//change to demo1 directory
cd demo1










