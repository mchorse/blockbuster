<?php

/**
 * PHP script which is responsible for reporting which language keys are missing 
 * for specific language file
 * 
 * @author mchorse
 */

/** Variables */
$here = __DIR__;
$target = "$here/../src/main/java/mchorse/blockbuster/";
$lang = "$here/../src/main/resources/assets/blockbuster/lang/en_US.lang";

$lang_content = file_get_contents($lang);
$iterator = new \RecursiveDirectoryIterator($target);
$iterator = new \RecursiveIteratorIterator($iterator);

$files = iterator_to_array($iterator);
$regex = '/"(?:[\w\d_]+\.)?(?<!mchorse\.)blockbuster(?:\.[\w\d_]+)+"/i';

$strings = [];
$inserts = [];

/**
 * String blacklist. Following strings are blacklisted and shouldn't be 
 * displayed if are found missing.
 */
$blacklist = [
    'blockbuster.actors',
    'blockbuster.Actor',
    'blockbuster.director',
    'blockbuster.actor_config',
    'blockbuster.playback',
    'blockbuster.register'
];

/** Starting output */
echo "\n\e[2;37mLooking up for files...\e[0m\n\n";

/** Collect data */
foreach ($files as $file)
{
    if (!$file->isFile() || strpos($file, '.DS_Store') !== false)
    {
        continue;
    }
    
    $path = substr($file, strlen($target));
    preg_match_all($regex, file_get_contents($file), $matches);
    
    if (count($matches[0]))
    {
        foreach ($matches[0] as $match)
        {
            $match = str_replace('"', '', $match);
            
            if (!strpos($lang_content, "\n" . $match . '=') !== false || in_array($match, $strings))
            {
                array_push($strings, $match);
                array_push($inserts, $path);
            }
        }
    }
}

/** Sort out */
$ordered = [];

foreach ($strings as $i => $string)
{
    if (in_array($string, $blacklist))
    {
        continue;
    }
    
    $ordered[$inserts[$i]][] = $string;
}

/** Finally output */
foreach ($ordered as $file => $strings)
{
    echo "File \e[0;33m\"$file\"\e[0m has:\n";
    
    foreach ($strings as $string)
        echo " - \e[1;37m$string\e[0m\n";
    
    
    echo "\n";
}