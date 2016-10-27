<?php

/**
 * Simple PHP script which converts YAML + Markdown like help into .ini like 
 * help file with Minecraft formatting.
 */

require 'vendor/autoload.php';

/**
 * From StackOverflow. This function checks if given array is an associative 
 * array.
 */
function is_assoc(array $arr)
{
    if (array() === $arr) 
    {
        return false;
    }
    
    return array_keys($arr) !== range(0, count($arr) - 1);
}

/**
 * Check whether a file is suffixed with YML extension.
 */
function is_yml($file)
{
    return preg_match('/\.ya?ml$/i', $file);
}

/**
 * Flatten out array multi-dimensional array.
 */
function flatten_array($array, $prefix = '')
{
    $output = [];
    $prefix = $prefix ? "$prefix." : '';
    
    foreach ($array as $key => $value)
    {
        $key = $prefix . $key;
        
        /** Spyc, for some reason, puts | and > strings into arrays */
        if (is_array($value) && !is_assoc($value))
        {   
            $value = implode("\n", $value);
        }
        
        if (is_array($value))
        {
            $output = array_merge($output, flatten_array($value, $key));
        }
        else
        {
            $output[$key] = $value;
        }
    }
    
    ksort($output, SORT_NATURAL);
    
    return $output;
}

/**
 * Convert given array to INI (assuming that input array isn't multi-dimensional 
 * and consist only out of strings)
 */
function to_ini(array $array)
{
    $string = '';
    
    foreach ($array as $key => $value)
    {
        $value = str_replace("\n", "\\n", $value);
        $value = process_formatting($value);
        
        $string .= "$key=$value\n";
    }
    
    return $string;
}

/**
 * Process formatting. This function simply replaces {r} into §r and {7} into 
 * §7. Basically because {7} is much readable than §7 with surrounding text.
 */
function process_formatting($value)
{
    return preg_replace('/\{([\w\d_]+)\}/', '§$1', $value);
}

/**
 * Compile given YAML files into one language file designated at $output.
 */
function compile($files, $output)
{
    $content = "#PARSE_ESCAPES\n\n";
    
    foreach ($files as $file)
    {
        $strings = spyc_load_file($file);
        $strings = flatten_array($strings);
        $name = pathinfo($file, PATHINFO_FILENAME);
        
        $content .= "# $name.yml\n". to_ini($strings) . "\n";
    }
    
    file_put_contents($output, trim($content));
}

echo "\n\e[2;37mStarting language compilation...\e[0m\n\n";

/** Starting the script */
$here = __DIR__;
$target = "$here/../help";
$langs = realpath("$here/../src/main/resources/assets/blockbuster/lang");
$files = scandir($target);
$files = array_slice($files, 2);

foreach ($files as $file)
{
    $original = $file;
    $output = "$langs/$file.lang";
    $file = "$target/$file";
    
    if (is_dir($file))
    {
        $lang_files = scandir($file);
        $lang_files = array_slice($lang_files, 2);
        $lang_files = array_filter($lang_files, 'is_yml');
        $count = count($lang_files);
        
        foreach ($lang_files as $k => $v)
        {
            $lang_files[$k] = "$file/$v";
        }
        
        compile($lang_files, $output);
        
        echo "\e[0;32m-\e[0m Compiled \e[0;33m\"$original\"\e[0m to \e[0;33m\"$original.lang\"\e[0m from \e[2;37m$count\e[0m files\n";
    }
    else
    {
        echo "\e[0;31m-\e[0m Skipping file \e[0;33m\"$original\"\e[0m, since it's not a directory...\n";
    }
}

echo "\n";