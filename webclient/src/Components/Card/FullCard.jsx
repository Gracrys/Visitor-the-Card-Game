import React from "react";
import { PureComponent } from "react";
import Rectangle from "react-rectangle";

import { getCardColor, toKnowledgeString } from "../Helpers/Helpers";
import Fonts from "../Primitives/Fonts";
import "./css/Card.css";
import { withSize } from "react-sizeme";
import "../../fonts/Fonts.css";
import TextOnImage from "../Primitives/TextOnImage";

class FullCard extends PureComponent {
  state = { showDialog: false };

  render() {
    const {
      opacity,
      name,
      description,
      cost,
      type,
      knowledgeCost,
      borderColor,
      health,
      delay,
      loyalty,
      shield,
      reflect,
      size
    } = this.props;
    const backColor = borderColor ? borderColor : undefined; //"gainsboro";

    return (
      <div style={{ width: "100%", height: "100%", position: "relative" }}>
        <Fonts />
        <Rectangle
          aspectRatio={[63, 88]}
          style={{
            opacity: opacity,
            backgroundColor: backColor,
            borderRadius: size.width / 20 + "px",
            textAlign: "left"
          }}
        >
          <div
            className="card-inner"
            style={{
              backgroundColor: getCardColor(knowledgeCost),
              fontSize: size.width / 20 + "px",
              borderRadius: size.width / 25 + "px",
              border: "1px black solid"
            }}
          >
            {cost && (
              <div className="card-cost">
                <img
                  src={
                    process.env.PUBLIC_URL + "/img/card-components/energy.png"
                  }
                  style={{
                    maxWidth: "100%"
                  }}
                  alt=""
                />
                <div
                  className="card-cost-text"
                  style={{ fontSize: size.width / 13 + "px" }}
                >
                  {cost}
                </div>
              </div>
            )}

            {toKnowledgeString(knowledgeCost)
              .split("")
              .map((c, i) => (
                <div
                  className="card-knowledge"
                  style={{ top: 11 + i * 3 + "%" }}
                  key={i}
                >
                  <img
                    src={
                      process.env.PUBLIC_URL +
                      "/img/card-components/knowledge-" +
                      c +
                      ".png"
                    }
                    style={{
                      maxWidth: "100%"
                    }}
                    alt=""
                  />
                </div>
              ))}
            <div className="card-name">{name}</div>

            <div className="card-image">
              <img
                src={
                  process.env.PUBLIC_URL + "/img/placeholders/" + type + ".png"
                }
                style={{ maxWidth: "100%" }}
                alt=""
              />
            </div>

            <div className="card-type">{type}</div>
            <div
              className="card-description"
              style={{
                whiteSpace: "pre-wrap"
              }}
            >
              {description}
              {reflect ? "\nReflect:" + reflect : ""}
            </div>
          </div>
          <div
            style={{
              position: "absolute",
              bottom: "-2%",
              left: "1%",
              height: "10%",
              display: "flex",
              alignItems: "center"
            }}
          >
            {health && (
              <TextOnImage
                src={process.env.PUBLIC_URL + "/img/card-components/health.png"}
                text={health}
                min={15}
                scale={5}
                font={{ fontFamily: "Special Elite, cursive" }}
              />
            )}
            {shield && (
              <TextOnImage
                src={process.env.PUBLIC_URL + "/img/card-components/shield.png"}
                text={shield}
                min={15}
                scale={5}
                font={{ fontFamily: "Special Elite, cursive" }}
              />
            )}
            {loyalty && (
              <TextOnImage
                src={
                  process.env.PUBLIC_URL + "/img/card-components/loyalty.png"
                }
                text={loyalty}
                min={15}
                scale={5}
                font={{ fontFamily: "Special Elite, cursive" }}
              />
            )}
            {delay && (
              <TextOnImage
                src={process.env.PUBLIC_URL + "/img/card-components/delay.png"}
                text={delay}
                min={15}
                scale={5}
                font={{ fontFamily: "Special Elite, cursive" }}
              />
            )}
          </div>
        </Rectangle>
      </div>
    );
  }
}

export default withSize({ monitorHeight: true })(FullCard);
