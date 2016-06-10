<?php
/**
 * Makes a request to AWIS for site info.
 */
class UrlInfo {

    protected static $ActionName        = 'TrafficHistory';
    protected static $ResponseGroupName = 'History';
    protected static $ServiceHost       = 'awis.amazonaws.com';
    protected static $Range             = 31;  // x<=31
    protected static $StartDate         = 20160701;
    protected static $SigVersion        = '2';
    protected static $HashAlgorithm     = 'HmacSHA256';

    public function UrlInfo($accessKeyId, $secretAccessKey, $site) {
        $this->accessKeyId = $accessKeyId;
        $this->secretAccessKey = $secretAccessKey;
        $this->site = $site;
    }

    /**
     * Get site info from AWIS.
     */ 
    public function getUrlInfo() {
        $queryParams = $this->buildQueryParams();
        $sig = $this->generateSignature($queryParams);
        $url = 'http://' . self::$ServiceHost . '/?' . $queryParams . 
            '&Signature=' . $sig;
        $response = self::makeRequest($url);
        $xmlResponse = self::parseResponse($response);
        return $xmlResponse;
    }

    /**
     * Builds current ISO8601 timestamp.
     */
    protected static function getTimestamp() {
        return gmdate("Y-m-d\TH:i:s.\\0\\0\\0\\Z", time()); 
    }

    /**
     * Builds query parameters for the request to AWIS.
     * Parameter names will be in alphabetical order and
     * parameter values will be urlencoded per RFC 3986.
     * @return String query parameters for the request
     */
    protected function buildQueryParams() {
        $params = array(
            'Action'            => self::$ActionName,
            'ResponseGroup'     => self::$ResponseGroupName,
            'AWSAccessKeyId'    => $this->accessKeyId,
            'Timestamp'         => self::getTimestamp(),
            'Range'             => self::$Range,
            'Start'             => self::$StartDate,
            'SignatureVersion'  => self::$SigVersion,
            'SignatureMethod'   => self::$HashAlgorithm,
            'Url'               => $this->site
        );
        ksort($params);
        $keyvalue = array();
        foreach($params as $key => $value) {
            $keyvalue[] = $key . '=' . rawurlencode($value);
        }
        return implode('&',$keyvalue);
    }

    /**
     * Makes request to AWIS
     * @param String $url   URL to make request to
     * @return String       Result of request
     */
    protected static function makeRequest($url) {
        $ch = curl_init($url);
        curl_setopt($ch, CURLOPT_TIMEOUT, 4);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
        $result = curl_exec($ch);
        curl_close($ch);
        return $result;
    }

    /**
     * Parses XML response from AWIS and displays selected data
     * @param String $response    xml response from AWIS
     */
    protected function parseResponse($response) {

        //last argument is namespace
        $xml = new SimpleXMLElement($response,null,false,
                                    'http://awis.amazonaws.com/doc/2005-07-11');

        if($xml->count() && $xml->Response->TrafficHistoryResult->Alexa->count()) {
            $info = $xml->Response->TrafficHistoryResult->Alexa->TrafficHistory;
        }
        return $info;
    }

    /**
     * Generates an HMAC signature per RFC 2104.
     *
     * @param String $url       URL to use in createing signature
     */
    protected function generateSignature($url) {
        $sign = "GET\n" . strtolower(self::$ServiceHost) . "\n/\n". $url;
        $sig = base64_encode(hash_hmac('sha256', $sign, $this->secretAccessKey, true));
        return rawurlencode($sig);
    }

}


$accessKeyId = $argv[1];
$secretAccessKey = $argv[2];
$site = $argv[3];


$trafficHistory = new UrlInfo($accessKeyId, $secretAccessKey, $site);
$xmlInfo = $trafficHistory->getUrlInfo();

$results = array(
    'Range'  => $xmlInfo->Range,
    'Site'   => $xmlInfo->Site,
    'Start'  => $xmlInfo->Start

);

echo "\nResults for " . $site .":\n\n";


foreach($results as $key => $value) {
    echo $key . ': ' . $value ."\n";
}


?>
