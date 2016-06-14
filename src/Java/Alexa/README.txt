######################
##                  ##
##  Alexa web info  ##
##                  ##
######################

- This tool searches the Alexa web information service to get useful informations on a particular website.

- To use this tool you will need to sign in to AWIS : http://aws.amazon.com/awis/ and to create an access key (Security Credentials -> Access Keys -> Create new access key).

- When ready, launch the tool by executing "execute.command". It will ask you for your access keys, output folder (where query results will be saved) and input csv file.

- Please note that the input csv file must have two columns : first one (column A) containing the name of the company and second one (column B) containing the URL for the company's website.

- As this tool is based on the URL, note that only lines containing a name AND a valid URL will be processed.

- You can now open your generated csv file with an office suite or google spreadsheet. This output file will be organised as follows : 
Column A will contain the site's global ranking, 
Column B will contain the name of the company,
Column C will contain the URL of their website,
Columns D to H will contain the five country that most browse the website with two informations : the percentage of visits coming from this country and the URL's rank in this country. 

######################

- To export a google spreadsheet to a csv file : (In the Google spreadSheet) File -> Download as -> Comma-Separated Values (CSV).

- To import a csv file to google spreadsheet : (In a blank Google spreadSheet) File -> Import -> Upload.

- To sort a whole sheet by a column (Global rank for example) :
	(In the Google spreadSheet) Select the column -> Data -> sort sheet by column A
	(In a LibreOffice spreadSheet) Data -> sort... -> Sort key 1 = column A






















