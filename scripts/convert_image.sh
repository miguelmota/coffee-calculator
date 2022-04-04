#!/bin/env bash

for filename in public/images/original/*; do
  image_filename="$(basename $filename)"
  image_basename="${image_filename%%.*}"
  size=256

  in="public/images/original/$image_filename"
  out="public/images/devices/$image_basename.png"
  convert $in -resize $size $out
  echo $out
done
echo done

