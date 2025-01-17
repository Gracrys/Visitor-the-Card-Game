import React from "react";
import { Component } from "react";
import Fonts from "./Fonts";

import "./TextOnImage.css";

class TextOnImage extends Component {
  render() {
    const { text, min, max, scale, src, font, style, imgStyle, textStyle, windowDimensions } = this.props;
    const windowWidth  = windowDimensions.width;
    const width = windowWidth / 5;
    const min_ = min? min : 1;
    const max_ = max?max:100;
    const scale_ = scale?scale:10;
    const font_ = font
      ? font
      : { fontFamily: "Cinzel, serif", fontWeight: "700" };
    return (
      <div className="container" style={style}>
        <Fonts />
        <img
          className="image"
          src={src}
          alt=""
          style={{
            maxWidth: "100%",
            maxHeight: "100%",
            width: "auto",
            height: "auto",
            objectFit: "scale-down",
            ...imgStyle
          }}
        />
        <div
          className="centered"
          style={{
            ...font_,
            ...textStyle,
            textShadow:
              "-1px -1px 0 #000, 1px -1px 0 #000, -1px 1px 0 #000, 1px 1px 0 #000",
            fontSize: Math.min(Math.max(width / scale_, min_), max_) + "px"
          }}
        >
          {text}
        </div>
      </div>
    );
  }
}

export default TextOnImage;
