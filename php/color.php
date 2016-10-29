<?php

/**
 * Colorify text string according to processed Minecraft's color formatting 
 * to raw escape formatting sequence thing.
 */
function colorify($text)
{
    static $mapping = [
        /* Formatting */
        '{r}' => "\e[0m",
        
        /* Colors */
        '{7}' => "\e[2;37m",
        '{a}' => "\e[0;32m",
        '{c}' => "\e[0;31m",
        '{e}' => "\e[0;33m",
        '{f}' => "\e[1;37m"
    ];
    
    return str_replace(array_keys($mapping), array_values($mapping), $text);
}