#!/bin/env bash

for filename in public/images/original/*; do
  image_filename="$(basename $filename)"
  image_basename="${image_filename%%.*}"
  size=128
  quality=60

  in="public/images/original/$image_filename"
  out="public/images/devices/$image_basename.webp"
  convert $in -resize $size -quality $quality -format webP $out
  echo $out
done
echo done

